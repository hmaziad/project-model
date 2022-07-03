// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyToolWindow {
    private static final String PROJECT_NOT_FOUND_ERROR_MESSAGE = "Please open a project to use this feature";
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
    private JLabel noFileSelectedLabel;
    private JButton saveDiffButton;
    private XTestCompositeNode node;
    private final Project project;

    public MyToolWindow(@NotNull Project project) {
        this.xDebuggerManager = XDebuggerManager.getInstance(project);
        computeChildrenService.initToolWindow(this);
        this.modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        this.modelActual.setRoot(null);
        this.fullPath = project.getBasePath() + DEBUG_FILES_PATH;
        this.project = project;
        updateJComboBox();
        initializeListeners();
    }

    private void initializeListeners() {
        this.diffSessionButton.addActionListener(e -> diffSession());
        this.targetFilesBox.addPopupMenuListener(new DropDownListener());
        this.saveSessionButton.addActionListener(e -> saveSessionInFile());
        this.saveDiffButton.addActionListener(e -> saveDiffInFile());
        this.baseFilesBox.addActionListener(e -> updateJComboBox());
        this.diffFilesButton.addActionListener(e -> diffFiles());
    }

    private void saveDiffInFile() {
        // do nothing
    }

    private void loadDebuggerSession() {
        XDebugSession currentSession = this.xDebuggerManager.getCurrentSession();
        XDebugSession session = Objects.requireNonNull(currentSession, START_DEBUGGER_ERROR_MESSAGE);
        log.info("Debug Session Retrieved...");
        computeChildrenService.initStackFrame(session.getCurrentStackFrame());
        log.info("Start Computing Children...");
    }

    private void diffSession() {
        loadDebuggerSession();
        //        CompletableFuture.runAsync(computeChildrenService::execute);
    }

    private void doDiffSession() {
        try {
            if (Objects.isNull(this.node)) {
                showDialogMessage();
                return;
            }
            String targetFileName = (String) targetFilesBox.getSelectedItem();
            List<String> original = Files.readAllLines(Paths.get(fullPath + targetFileName));
            // todo this needs to be refactored
            List<String> revised = Arrays.stream(Parser.deParse(this.node).split("\n")).collect(Collectors.toList());
            XTestCompositeNode diffNode = Helper.unifiedDiff(original, revised);
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
            XTestCompositeNode diffNode = Helper.unifiedDiff(original, revised);
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

    private void selectDebugFile() {
        String selectedFileName = (String) targetFilesBox.getSelectedItem();
        Path selectedPath = Paths.get(fullPath + selectedFileName);
        XTestCompositeNode parsedNode = Parser.parse(selectedPath);
        this.modelActual.setRoot(parsedNode);
        //        this.noFileSelectedLabel.setVisible(false);
    }

    private void saveSessionInFile() {
        loadDebuggerSession();
        Consumer<XTestCompositeNode> saveSessionConsumer = this::saveSession;
        CompletableFuture.runAsync(() -> computeChildrenService.execute(saveSessionConsumer));
    }

    private void saveSession(XTestCompositeNode computedNode) {
        String newFolder = project.getBasePath() + DEBUG_FILES_PATH;
        try {
            Files.createDirectories(Paths.get(newFolder));
            Files.writeString(Paths.get(newFolder + new Date().getTime() + ".txt"), Parser.deParse(computedNode));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setTreeView(XTestCompositeNode node) {
        this.node = node;
        this.modelActual.setRoot(node);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private class DropDownListener implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            updateJComboBox();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // nothing
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            // nothing
        }
    }
}
