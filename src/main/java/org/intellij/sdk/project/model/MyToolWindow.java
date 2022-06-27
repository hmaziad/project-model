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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MyToolWindow {

    private static final String DEBUG_FILES_PATH = "/src/main/resources/debugFiles/";
    private final DefaultTreeModel modelActual;
    private final String fullPath;
    private JPanel myToolWindowContent;
    private JTree myTreeActual;
    private JButton diffFilesButton;
    private JButton saveButton;
    private JComboBox<String> targetFiles;
    private JComboBox<String> baseFiles;
    private JButton diffSessionButton;
    private JLabel noFileSelectedLabel;
    private XTestCompositeNode node;
    private final Project project;

    public MyToolWindow(@NotNull Project project) {
        this.modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        this.modelActual.setRoot(null);

        this.fullPath = project.getBasePath() + DEBUG_FILES_PATH;
        this.project = project;
        updateJComboBox();
        saveButton.addActionListener(e -> saveNodeInFile());
        targetFiles.addActionListener(e -> selectDebugFile());
        diffFilesButton.addActionListener( e -> diffFiles());
        diffSessionButton.addActionListener( e -> diffSession());
    }

    private void diffSession() {
        try {
            if (Objects.isNull(this.node)) {
                showDialogMessage();
                return;
            }
            String targetFileName = (String) targetFiles.getSelectedItem();
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
        String targetFileName = (String) targetFiles.getSelectedItem();
        String baseFileName = (String) baseFiles.getSelectedItem();
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
            targetFiles.removeAllItems();
            baseFiles.removeAllItems();
            files.forEach(this::updateFileNames);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void updateFileNames(Path path) {
        targetFiles.addItem(path.getFileName().toString());
        baseFiles.addItem(path.getFileName().toString());
    }

    private void selectDebugFile() {
        String selectedFileName = (String) targetFiles.getSelectedItem();
        Path selectedPath = Paths.get(fullPath + selectedFileName);
        XTestCompositeNode parsedNode = Parser.parse(selectedPath);
        this.modelActual.setRoot(parsedNode);
        this.noFileSelectedLabel.setVisible(false);
    }

    private void saveNodeInFile() {
        if (Objects.isNull(this.node)) {
            showDialogMessage();
            return;
        }
        String newFolder = project.getBasePath() + DEBUG_FILES_PATH;
        try {
            Files.createDirectories(Paths.get(newFolder));
            Files.writeString(Paths.get(newFolder + new Date().getTime() + ".txt"), Parser.deParse(this.node));
            updateJComboBox();
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
}
