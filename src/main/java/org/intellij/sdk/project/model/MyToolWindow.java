// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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

    private final DefaultTreeModel modelActual;
    private final XDebuggerManager xDebuggerManager;
    private JPanel myToolWindowContent;
    private JTree myTreeActual;
    private JButton diffFilesButton;
    private JButton saveSnapShotButton;
    private JComboBox<String> targetFilesBox;
    private JComboBox<String> baseFilesBox;
    private JButton diffSessionButton;
    private JButton saveDiffButton;
    private JButton UpButton;
    private JButton downButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private final Project project;
    private List<String> diffLines;
    private List<XTestCompositeNode> diffNodes = new ArrayList<>();
    private int index = -1;

    public MyToolWindow(@NotNull Project project) {
        this.xDebuggerManager = XDebuggerManager.getInstance(project);
        computeChildrenService.initToolWindow(this);
        this.modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        this.modelActual.setRoot(null);
        this.myTreeActual.setRootVisible(false);
        this.project = project;
        initializeListeners();
    }

    private void initializeListeners() {
        this.diffSessionButton.addActionListener(e -> diffSession());
        this.UpButton.addActionListener(e -> navigateUp());
        this.downButton.addActionListener(e -> navigateDown());
        this.targetFilesBox.addActionListener(e -> selectTargetFile());
        this.saveSnapShotButton.addActionListener(e -> saveSnapShot());
        this.deleteButton.addActionListener(e -> deleteSnap());
//        this.saveDiffButton.addActionListener(e -> saveDiffInFile());
//        this.baseFilesBox.addActionListener(e -> updateJComboBox());
//        this.diffFilesButton.addActionListener(e -> diffFiles());
        this.refreshButton.addActionListener(e -> {
            this.targetFilesBox.removeAllItems();
            persistencyService.getNodes().keySet().forEach(this.targetFilesBox::addItem);
        });
    }

    private void deleteSnap() {
        Object currentItem = this.targetFilesBox.getSelectedItem();
        persistencyService.getNodes().remove((String) currentItem);
        this.targetFilesBox.removeItem(currentItem);
    }

    private void navigateDown() {
        if (diffNodes.isEmpty() || this.index == diffNodes.size() - 1) {
            return;
        }
        this.index++;
        updateTreeSelectionAndScroll();
    }

    private void navigateUp() {
        if (diffNodes.isEmpty() || this.index == 0) {
            return;
        }
        this.index--;
        updateTreeSelectionAndScroll();
    }

    private void updateTreeSelectionAndScroll() {
        TreePath nodePath = new TreePath(diffNodes.get(this.index).getPath());
        this.myTreeActual.expandPath(nodePath);
        this.myTreeActual.setSelectionPath(nodePath);
        Rectangle bounds = myTreeActual.getPathBounds(nodePath);
        bounds.height = myTreeActual.getVisibleRect().height;
        myTreeActual.scrollRectToVisible(bounds);
    }

    private void diffSession() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(this::diffDebuggerSession));
    }

    private void diffDebuggerSession(XTestCompositeNode node) {

        String targetFileName = (String) targetFilesBox.getSelectedItem();
        //            List<String> original = Files.readAllLines(Paths.get(fullPath + targetFileName));
        List<String> original = new ArrayList<>();
        // todo this needs to be refactored
        List<String> revised = Arrays //
            .stream(Parser.writeNodeAsString(node).split("\n")) //
            .collect(Collectors.toList());

        this.diffLines = Parser.unifiedDiffOfStrings(original, revised);
        XTestCompositeNode diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
        this.modelActual.setRoot(diffNode);
    }

    private void diffFiles() {
        String targetFileName = (String) targetFilesBox.getSelectedItem();
        String baseFileName = (String) baseFilesBox.getSelectedItem();
        List<String> original = new ArrayList<>();
        List<String> revised = new ArrayList<>();
        this.diffLines = Parser.unifiedDiffOfStrings(original, revised);
        this.diffNodes.clear();
        XTestCompositeNode diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
        this.modelActual.setRoot(diffNode);
    }

    private void selectTargetFile() {
        String selectedFileName = (String) targetFilesBox.getSelectedItem();
//        Path selectedPath = Paths.get(fullPath + selectedFileName);
//        XTestCompositeNode parsedNode = Parser.parseStringsToNode(selectedPath);
        this.modelActual.setRoot(persistencyService.getNodes().get(selectedFileName));
        this.myTreeActual.setRootVisible(false);
    }

    private void saveSnapShot() {
        String snapName = "Snap-" + new Date().getTime();
        this.targetFilesBox.addItem(snapName); // this also freezes Persistency service if written after computing the node
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
