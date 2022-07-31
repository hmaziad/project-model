// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
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

    private final NodeNavigator nodeNavigator;
    private final DefaultTreeModel modelActual;
    private final XDebuggerManager xDebuggerManager;

    private JPanel myToolWindowContent;
    private JTree nodeTree;
    private JButton diffSnapsButton;
    private JButton saveSnapShotButton;
    private JComboBox<String> sourceShotsBox;
    private JComboBox<String> otherShotsBox;
    private JButton diffShotButton;
    private JButton saveDiffShotButton;
    private JButton UpButton;
    private JButton downButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private List<String> diffLines;
    private List<XTestCompositeNode> diffNodes = new ArrayList<>();
    private XTestCompositeNode diffNode;

    public MyToolWindow(@NotNull Project project) {
        this.xDebuggerManager = XDebuggerManager.getInstance(project);
        computeChildrenService.initToolWindow(this);
        this.modelActual = (DefaultTreeModel) this.nodeTree.getModel();
        this.modelActual.setRoot(null);
        this.nodeTree.setRootVisible(false);
        this.nodeNavigator = new NodeNavigator(this.diffNodes, this.nodeTree);
        initializeListeners();
    }

    private void initializeListeners() {
        this.diffShotButton.addActionListener(e -> {
            this.nodeNavigator.reset();
            diffCurrentSessionWithSnapShot();
        });
        this.diffSnapsButton.addActionListener(e -> {
            this.nodeNavigator.reset();
            diffSnaps();
        });
        this.UpButton.addActionListener(e -> this.nodeNavigator.navigateUp());
        this.downButton.addActionListener(e -> this.nodeNavigator.navigateDown());
        this.sourceShotsBox.addActionListener(e -> {
            selectShot();
            this.nodeNavigator.reset();
        });
        this.otherShotsBox.addActionListener(e -> {
            this.nodeNavigator.reset();
        });
        this.saveSnapShotButton.addActionListener(e -> saveSnapShot());
        this.deleteButton.addActionListener(e -> deleteShot());
        this.saveDiffShotButton.addActionListener(e -> saveDiffShot());
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
    }

    private void diffSnaps() {
        String selectedSourceName = (String) this.sourceShotsBox.getSelectedItem();
        String selectedTargetName = (String) this.otherShotsBox.getSelectedItem();
        XTestCompositeNode selectedSourceNode = persistencyService.getNodes().get(selectedSourceName);
        XTestCompositeNode selectedTargetNode = persistencyService.getNodes().get(selectedTargetName);
        List<String> selectedSourceAsString = Parser.writeNodeAsString(selectedSourceNode);
        List<String> selectedTargetAsString = Parser.writeNodeAsString(selectedTargetNode);
        this.diffLines = Parser.unifiedDiffOfStrings(selectedSourceAsString, selectedTargetAsString);
        this.diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
        LOG.debug("Diff Node: {}", this.diffNode);
        this.modelActual.setRoot(this.diffNode);
    }

    private void diffCurrentSessionWithSnapShot() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(this::diffDebuggerShot));
    }

    private void selectShot() {
        String selectedSourceName = (String) this.sourceShotsBox.getSelectedItem();
        XTestCompositeNode root = persistencyService.getNodes().get(selectedSourceName);
        this.modelActual.setRoot(root);
        this.nodeTree.setRootVisible(false);
    }

    private void saveSnapShot() {
        String snapName = SNAP + new Date().getTime();
        this.sourceShotsBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
        this.otherShotsBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(computedNode -> persistencyService.addNode(snapName, computedNode)));
    }

    private void saveDiffShot() {
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

    private void diffDebuggerShot(XTestCompositeNode computedNode) {
        String targetSnapShotName = (String) sourceShotsBox.getSelectedItem();
        List<String> targetNodeAsStrings = Parser.writeNodeAsString(persistencyService.getNodes().get(targetSnapShotName));
        LOG.debug("Target: {}", targetNodeAsStrings);
        List<String> sourceNodeAsStrings = Parser.writeNodeAsString(computedNode);
        LOG.debug("Source: {}", sourceNodeAsStrings);
        this.diffLines = Parser.unifiedDiffOfStrings(targetNodeAsStrings, sourceNodeAsStrings);
        LOG.debug("Diff Lines: {}", this.diffLines);
        this.diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
        LOG.debug("Diff Node: {}", this.diffNode);
        this.modelActual.setRoot(this.diffNode);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private void loadDebuggerSession() {
        XDebugSession currentSession = this.xDebuggerManager.getCurrentSession();
        XDebugSession session = Objects.requireNonNull(currentSession, START_DEBUGGER_ERROR_MESSAGE);
        LOG.info("Debug Session Retrieved...");
        computeChildrenService.initStackFrame(session.getCurrentStackFrame());
        LOG.info("Start Computing Children...");
    }
}
