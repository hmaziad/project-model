// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MyToolWindow {
    private static final String START_DEBUGGER_ERROR_MESSAGE = "Please start debugger to use this feature";
    private static final ComputeChildrenService computeChildrenService = new ComputeChildrenService();
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private static final String SNAP = "Snap-";
    private static final String DIFF = "Diff-";

    private final NodeNavigatorService nodeNavigatorService;
    private final DefaultTreeModel modelActual;
    private final XDebuggerManager xDebuggerManager;
    private Project project;


    private JPanel myToolWindowContent;
    private JTree nodeTree;
    private JButton diffSnapsButton;
    private JButton saveSnapShotButton;
    private JComboBox<String> sourceShotsBox;
    private JComboBox<String> otherShotsBox;
    private JButton diffShotButton;
    private JButton saveDiffShotButton;
    private JButton upButton;
    private JButton downButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private JButton takeSnapshot;
    private List<String> diffLines;
    private List<XTestCompositeNode> diffNodes = new ArrayList<>();
    private XTestCompositeNode diffNode;
    private XTestCompositeNode computedNode;
    private AtomicBoolean isDiffShot = new AtomicBoolean();

    public MyToolWindow(@NotNull Project project) {
        this.xDebuggerManager = XDebuggerManager.getInstance(project);
        this.project = project;
        this.modelActual = (DefaultTreeModel) this.nodeTree.getModel();
        this.modelActual.setRoot(null);
        this.nodeTree.setRootVisible(false);
        this.nodeNavigatorService = new NodeNavigatorService(this.diffNodes, this.nodeTree);
        initializeListeners();
        this.nodeTree.setCellRenderer(new DebuggerTreeRenderer());
    }

    private void initializeListeners() {
        this.takeSnapshot.addActionListener(e -> takeSnapshot());
        this.saveSnapShotButton.addActionListener(e -> saveSnapShot());
        this.diffShotButton.addActionListener(e -> diffCurrentSessionWithSnapShot());
        this.saveDiffShotButton.addActionListener(e -> saveDiffShot());
        this.upButton.addActionListener(e -> {
            if (!this.isDiffShot.get()) {
                Messages.showMessageDialog(this.project, "Please diff a node first", "Error", Messages.getInformationIcon());
                return;
            }
            this.nodeNavigatorService.navigateUp();
        });
        this.downButton.addActionListener(e -> {
            if (!this.isDiffShot.get()) {
                Messages.showMessageDialog(this.project, "Please diff a node first", "Error", Messages.getInformationIcon());
                return;
            }
            this.nodeNavigatorService.navigateDown();
        });
        this.sourceShotsBox.addActionListener(e -> selectShot());
        this.otherShotsBox.addActionListener(e -> this.nodeNavigatorService.reset());
        this.deleteButton.addActionListener(e -> deleteShot());
        this.refreshButton.addActionListener(e -> {
            this.sourceShotsBox.removeAllItems();
            this.otherShotsBox.removeAllItems();
            persistencyService.getNodes().keySet().forEach(shotName -> {
                this.sourceShotsBox.addItem(shotName);
                if (shotName.startsWith(SNAP)) {
                    this.otherShotsBox.addItem(shotName);
                }
            });
        });
        this.diffSnapsButton.addActionListener(e -> diffSnaps());

    }

    private void takeSnapshot() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(resultComputedNode -> {
            this.computedNode = resultComputedNode;
            this.modelActual.setRoot(this.computedNode);
            isDiffShot.set(false);
        }));
    }

    private void diffSnaps() {
        this.nodeNavigatorService.reset();
        String selectedSourceName = (String) this.sourceShotsBox.getSelectedItem();
        String selectedTargetName = (String) this.otherShotsBox.getSelectedItem();
        XTestCompositeNode selectedSourceNode = persistencyService.getNodes().get(selectedSourceName);
        XTestCompositeNode selectedTargetNode = persistencyService.getNodes().get(selectedTargetName);
        List<String> selectedSourceAsString = ParserService.writeNodeAsString(selectedSourceNode);
        List<String> selectedTargetAsString = ParserService.writeNodeAsString(selectedTargetNode);
        this.diffLines = ParserService.unifiedDiffOfStrings(selectedSourceAsString, selectedTargetAsString);
        this.diffNode = ParserService.parseStringsToNode(this.diffLines, this.diffNodes);
        LOG.debug("Diff Node: {}", this.diffNode);
        this.modelActual.setRoot(this.diffNode);
        isDiffShot.set(true);
    }

    private void diffCurrentSessionWithSnapShot() {
        this.nodeNavigatorService.reset();
        StringBuilder messageBuilder = new StringBuilder();
        if (Objects.isNull(this.computedNode)) {
            messageBuilder.append("Please take a snap shot first");
        }
        Object selectedItem = sourceShotsBox.getSelectedItem();
        if (Objects.isNull(selectedItem)) {
            messageBuilder.append("\nPlease take select a ref snap shot from dropdown button");
        }
        if (messageBuilder.length() != 0) {
            Messages.showMessageDialog(this.project, messageBuilder.toString(), "Error", Messages.getInformationIcon());
            return;
        }
        String targetSnapShotName = (String) selectedItem;
        List<String> targetNodeAsStrings = ParserService.writeNodeAsString(persistencyService.getNodes().get(targetSnapShotName));
        LOG.debug("Target: {}", targetNodeAsStrings);
        List<String> sourceNodeAsStrings = ParserService.writeNodeAsString(this.computedNode);
        LOG.debug("Source: {}", sourceNodeAsStrings);
        this.diffLines = ParserService.unifiedDiffOfStrings(targetNodeAsStrings, sourceNodeAsStrings);
        LOG.debug("Diff Lines: {}", this.diffLines);
        this.diffNode = ParserService.parseStringsToNode(this.diffLines, this.diffNodes);
        if (this.diffNodes.isEmpty()) {
            Messages.showMessageDialog(this.project, "Nodes are identical", "Info", Messages.getInformationIcon());
            return;
        }
        LOG.debug("Diff Node: {}", this.diffNode);
        this.modelActual.setRoot(this.diffNode);
        this.isDiffShot.set(true);
    }

    private void selectShot() {
        this.nodeNavigatorService.reset();
        String selectedSourceName = (String) this.sourceShotsBox.getSelectedItem();
        XTestCompositeNode root = persistencyService.getNodes().get(selectedSourceName);
        this.modelActual.setRoot(root);
        this.nodeTree.setRootVisible(false);
    }

    private void saveSnapShot() {
        if (Objects.isNull(this.computedNode)) {
            Messages.showMessageDialog(this.project, "please take a snapshot first", "Error", Messages.getInformationIcon());
            return;
        }
        String snapName = SNAP + new Date().getTime();
        this.sourceShotsBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
        this.otherShotsBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
        persistencyService.addNode(snapName, this.computedNode);
    }

    private void saveDiffShot() {
        if (Objects.isNull(this.diffNode)) {
            Messages.showMessageDialog(this.project, "please diff a node first", "Error", Messages.getInformationIcon());
            return;
        }
        String diffName = DIFF + new Date().getTime();
        this.sourceShotsBox.addItem(diffName);
        persistencyService.addNode(diffName, this.diffNode);
    }

    private void deleteShot() {
        Object currentShot = this.sourceShotsBox.getSelectedItem();
        String currentShotName = (String) currentShot;
        persistencyService.getNodes().remove(currentShotName);
        this.sourceShotsBox.removeItem(currentShot);
        if (Objects.nonNull(currentShotName) && currentShotName.startsWith(SNAP)) {
            this.otherShotsBox.removeItem(currentShot);
        }
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private void loadDebuggerSession() {
        XDebugSession currentSession = this.xDebuggerManager.getCurrentSession();
        if (Objects.isNull(currentSession)) {
            Messages.showMessageDialog(this.project, START_DEBUGGER_ERROR_MESSAGE, "Error", Messages.getInformationIcon());
            return;
        }
        LOG.info("Debug Session Retrieved...");
        computeChildrenService.initStackFrame(currentSession.getCurrentStackFrame());
        LOG.info("Start Computing Children...");
    }


}

class DebuggerTreeRenderer extends DefaultTreeCellRenderer {
    // Both methods are needed
    private JBColor theColor;

    @Override
    public Color getBackground() {
        return theColor;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return theColor;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        XTestCompositeNode node = (XTestCompositeNode) value;
        setIcon(node.getIcon());
        if (node.getDiffChar() == '+') {
            this.theColor = JBColor.GREEN;
        } else if (node.getDiffChar() == '-') {
            this.theColor = JBColor.RED;
        } else {
            this.theColor = null;
        }
        return this;
    }
}