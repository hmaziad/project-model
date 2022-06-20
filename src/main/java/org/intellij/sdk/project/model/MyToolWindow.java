// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;

public class MyToolWindow {

    private static final String DEBUG_FILES_PATH = "/src/main/resources/debugFiles/";
    private final DefaultTreeModel modelActual;
    private String FULL_PATH;
    private JPanel myToolWindowContent;
    private JTree myTreeActual;
    private JButton saveButton;
    private JComboBox<String> debugFiles;
    private XTestCompositeNode node;
    private Project project;

    public MyToolWindow(@NotNull Project project) {
        this.modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        this.project = project;
        this.FULL_PATH = project.getBasePath() + DEBUG_FILES_PATH;
        updateJComboBox();
        saveButton.addActionListener(e -> saveNodeInFile());
        debugFiles.addActionListener(e -> selectDebugFile());
    }

    private void updateJComboBox() {
        try (Stream<Path> files = Files.list(Paths.get(this.FULL_PATH))) {
            files.forEach(path -> debugFiles.addItem(path.getFileName().toString()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void selectDebugFile() {
        String selectedFileName = (String) debugFiles.getSelectedItem();
        Path selectedPath = Paths.get(FULL_PATH + selectedFileName);
        XTestCompositeNode parsedNode = Parser.parse(selectedPath);
        this.modelActual.setRoot(parsedNode);
    }

    private void saveNodeInFile() {
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
