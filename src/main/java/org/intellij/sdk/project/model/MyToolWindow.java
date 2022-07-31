// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    private final NodeNavigator nodeNavigator;
    private final DefaultTreeModel modelActual;
    private final XDebuggerManager xDebuggerManager;

    private JPanel myToolWindowContent;
    private JTree nodeTree;
    private JButton diffFilesButton;
    private JButton saveSnapShotButton;
    private JComboBox<String> sourceShotsBox;
    private JComboBox<String> baseFilesBox;
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
            diffShot();
        });
        this.UpButton.addActionListener(e -> this.nodeNavigator.navigateUp());
        this.downButton.addActionListener(e -> this.nodeNavigator.navigateDown());
        this.sourceShotsBox.addActionListener(e -> {
            selectTargetShot();
            this.nodeNavigator.reset();
        });
        this.saveSnapShotButton.addActionListener(e -> saveSnapShot());
        this.deleteButton.addActionListener(e -> deleteSnap());
        this.saveDiffShotButton.addActionListener(e -> saveDiffShot());
        this.refreshButton.addActionListener(e -> {
            this.sourceShotsBox.removeAllItems();
            persistencyService.getNodes().keySet().forEach(this.sourceShotsBox::addItem);
        });
    }

    private void saveDiffShot() {
        String diffName = "Diff-" + new Date().getTime();
        this.sourceShotsBox.addItem(diffName);
        persistencyService.addNode(diffName, this.diffNode);
    }

    private void deleteSnap() {
        Object currentItem = this.sourceShotsBox.getSelectedItem();
        persistencyService.getNodes().remove((String) currentItem);
        this.sourceShotsBox.removeItem(currentItem);
    }

    private void diffShot() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(this::diffDebuggerShot));
    }

    private void diffDebuggerShot(XTestCompositeNode node) {
        String targetSnapShotName = (String) sourceShotsBox.getSelectedItem();
        List<String> targetNodeAsStrings = Arrays //
            .stream(Parser.writeNodeAsString(persistencyService.getNodes().get(targetSnapShotName)).split("\n")) //
            .collect(Collectors.toList());
        LOG.debug("Target: {}", targetNodeAsStrings);

        List<String> sourceNodeAsStrings = Arrays //
            .stream(Parser.writeNodeAsString(node).split("\n")) //
            .collect(Collectors.toList());
        LOG.debug("Source: {}", sourceNodeAsStrings);

        this.diffLines = Parser.unifiedDiffOfStrings(targetNodeAsStrings, sourceNodeAsStrings);
        LOG.debug("Diff Lines: {}",  this.diffLines);

        this.diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
        LOG.debug("Diff Node: {}", this.diffNode);

        this.modelActual.setRoot(this.diffNode);
    }

    private void selectTargetShot() {
        String selectedFileName = (String) sourceShotsBox.getSelectedItem();
        this.modelActual.setRoot(persistencyService.getNodes().get(selectedFileName));
        this.nodeTree.setRootVisible(false);
    }

    private void saveSnapShot() {
        String snapName = "Snap-" + new Date().getTime();
        this.sourceShotsBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(computedNode ->  persistencyService.addNode(snapName, computedNode)));
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
