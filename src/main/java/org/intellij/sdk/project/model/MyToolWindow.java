// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;

public class MyToolWindow {

    private static final String DEBUG_FILES_PATH = "/src/main/resources/debugFiles/";
    private JPanel myToolWindowContent;
    private JTree myTreeActual;
    private JButton saveButton;
    private JComboBox debugFiles;
    private XTestCompositeNode node;
    private Project project;

    public MyToolWindow(@NotNull Project project) {
        this.project = project;
        try {
            Files.list(Paths.get(project.getBasePath() + DEBUG_FILES_PATH)).forEach(path -> debugFiles.addItem(path.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveButton.addActionListener(e -> saveNodeInFile());
        debugFiles.addActionListener(e -> selectDebugFile());
    }

    private void selectDebugFile() {
        String selectedFile = (String) debugFiles.getSelectedItem();

    }

    private void saveNodeInFile() {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node, "");
        String newFolder = project.getBasePath() + DEBUG_FILES_PATH;
        try {
            Files.createDirectories(Paths.get(newFolder));
            Files.writeString(Paths.get(newFolder + new Date().getTime() + ".txt"), sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addLines(StringBuilder sb, XTestCompositeNode node, String tab) {
        sb.append(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue() + "\n");
        node.getChildren().forEach(child -> addLines(sb, child, tab + "\t"));
    }

    public void setTreeView(XTestCompositeNode node) {
        this.node = node;
        DefaultTreeModel modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        modelActual.setRoot(node);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }
}
