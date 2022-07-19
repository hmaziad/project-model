// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class MyToolWindow {
    private static final String START_DEBUGGER_ERROR_MESSAGE = "Please start debugger to use this feature";
    private static final ComputeChildrenService computeChildrenService = new ComputeChildrenService();

    private static final String DEBUG_FILES_PATH = "/src/main/resources/debugFiles/";
    private final DefaultTreeModel modelActual;
    private final String fullPath;
    private final XDebuggerManager xDebuggerManager;
    private JPanel myToolWindowContent;
    private JTree myTreeActual;
    private JButton diffFilesButton;
    private JButton saveSessionButton;
    private JComboBox<String> targetFilesBox;
    private JComboBox<String> baseFilesBox;
    private JButton diffSessionButton;
    private JButton saveDiffButton;
    private JButton UpButton;
    private JButton downButton;
    private JButton refreshButton;
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
        this.fullPath = project.getBasePath() + DEBUG_FILES_PATH;
        this.project = project;
//        updateJComboBox();
        initializeListeners();
    }

    private void initializeListeners() {
        this.diffSessionButton.addActionListener(e -> diffSession());
        this.UpButton.addActionListener(e -> navigateUp());
        this.downButton.addActionListener(e -> navigateDown());
//        this.targetFilesBox.addActionListener(e -> selectTargetFile());
        this.saveSessionButton.addActionListener(e -> saveSessionInFile());
//        this.saveDiffButton.addActionListener(e -> saveDiffInFile());
//        this.baseFilesBox.addActionListener(e -> updateJComboBox());
//        this.diffFilesButton.addActionListener(e -> diffFiles());
//        this.refreshButton.addActionListener(e -> updateJComboBox());
//        this.refreshButton.addActionListener(e -> updateJComboBoxFromState());
    }

//    private void updateJComboBoxFromState() {
//        LOG.info("Updating target combo box");
//        this.targetFilesBox.removeAllItems();
//        PersistencyService //
//            .getInstance() //
//            .nodes //
//            .keySet() //
//            .forEach(this.targetFilesBox::addItem);
//    }

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

    private void saveDiffInFile() {
        try {
            String newFolder = project.getBasePath() + DEBUG_FILES_PATH;
            Files.createDirectories(Paths.get(newFolder));
            Path filePath = Paths.get(newFolder + "Diff-" + new Date().getTime() + ".txt");
            String diffAsString = Parser.writeNodeAsString(this.diffLines);
            Files.writeString(filePath, diffAsString);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void diffSession() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(this::diffDebuggerSession));
    }

    private void diffDebuggerSession(XTestCompositeNode node) {
        try {
            String targetFileName = (String) targetFilesBox.getSelectedItem();
            List<String> original = Files.readAllLines(Paths.get(fullPath + targetFileName));
            // todo this needs to be refactored
            List<String> revised = Arrays //
                .stream(Parser.writeNodeAsString(node).split("\n")) //
                .collect(Collectors.toList());

            this.diffLines = Parser.unifiedDiffOfStrings(original, revised);
            XTestCompositeNode diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
            this.modelActual.setRoot(diffNode);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void showDialogMessage() {
        Messages.showDialog(this.project, "Please Start a Debug Session", "No session created", new String[] {"ok"}, 0, null);
    }

    private void diffFiles() {
        String targetFileName = (String) targetFilesBox.getSelectedItem();
        String baseFileName = (String) baseFilesBox.getSelectedItem();
        try {
            List<String> original = Files.readAllLines(Paths.get(fullPath + baseFileName));
            List<String> revised = Files.readAllLines(Paths.get(fullPath + targetFileName));
            this.diffLines = Parser.unifiedDiffOfStrings(original, revised);
            this.diffNodes.clear();
            XTestCompositeNode diffNode = Parser.parseStringsToNode(this.diffLines, this.diffNodes);
            this.modelActual.setRoot(diffNode);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void updateJComboBox() {
        try (Stream<Path> files = Files.list(Paths.get(this.fullPath))) {
            int targetFilesBoxItemCount = this.targetFilesBox.getItemCount();
            Set<Path> updatedFilePaths = files.collect(Collectors.toSet());
            if (targetFilesBoxItemCount != updatedFilePaths.size()) { // need to refactor to check that files dif
                System.out.println(Thread.currentThread().getName() + ": 1");
                targetFilesBox.removeAllItems();
                baseFilesBox.removeAllItems();
                System.out.println(Thread.currentThread().getName() + ": 2");
                updatedFilePaths.stream().map(path -> path.getFileName().toString()).forEach(fileName -> {
                    targetFilesBox.addItem(fileName);
                    baseFilesBox.addItem(fileName);
                });
                System.out.println(Thread.currentThread().getName() + ": 3");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void selectTargetFile() {
        String selectedFileName = (String) targetFilesBox.getSelectedItem();
        Path selectedPath = Paths.get(fullPath + selectedFileName);
        XTestCompositeNode parsedNode = Parser.parseStringsToNode(selectedPath);
        this.modelActual.setRoot(parsedNode);
        this.myTreeActual.setRootVisible(false);
    }

    private void saveSessionInFile() {
        loadDebuggerSession();
        CompletableFuture.runAsync(() -> computeChildrenService.execute(this::persistNode));
    }

    private void persistNode(XTestCompositeNode computedNode) {
        String snapName = "Snap-" + new Date().getTime();
        Map<String, XTestCompositeNode> newMap = Map.of(snapName, computedNode);
        PersistencyService.State state1 = new PersistencyService.State();
        state1.stateNodes = newMap;
        PersistencyService persistencyService = PersistencyService.getInstance();
        persistencyService.loadState(state1);
        persistencyService.getState();
    }


    private void saveSession(XTestCompositeNode computedNode) {
        String newFolder = project.getBasePath() + DEBUG_FILES_PATH;
        try {
            Files.createDirectories(Paths.get(newFolder));
            Path filePath = Paths.get(newFolder + "Snap-" + new Date().getTime() + ".txt");
            String nodeAsString = Parser.writeNodeAsString(computedNode);
            Files.writeString(filePath, nodeAsString);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
