// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
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
    private JComboBox diffAgainst;
    private JButton diff;
    private XTestCompositeNode node;
    private Project project;

    public MyToolWindow(@NotNull Project project) {
        this.modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
//        this.myTreeActual.setCellRenderer(new DebugTreeRenderer());

        this.project = project;
        this.FULL_PATH = project.getBasePath() + DEBUG_FILES_PATH;
        updateJComboBox();
        saveButton.addActionListener(e -> saveNodeInFile());
        debugFiles.addActionListener(e -> selectDebugFile());
        diff.addActionListener( e -> doDiff());
    }

    private void doDiff() {
        String targetFileName = (String) debugFiles.getSelectedItem();
        String baseFileName = (String) diffAgainst.getSelectedItem();
        XTestCompositeNode diffNode = Helper.unifiedDiff(FULL_PATH + baseFileName, FULL_PATH + targetFileName);
        this.modelActual.setRoot(diffNode);
    }

    private void updateJComboBox() {
        try (Stream<Path> files = Files.list(Paths.get(this.FULL_PATH))) {
            files.forEach(path -> updateFileNames(path));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void updateFileNames(Path path) {
        debugFiles.addItem(path.getFileName().toString());
        diffAgainst.addItem(path.getFileName().toString());
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

    private class DebugTreeRenderer extends DefaultTreeCellRenderer {
        private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            System.out.println(getText());
            if (value instanceof XTestCompositeNode) {
                XTestCompositeNode value1 = (XTestCompositeNode) value;
                System.out.println(value1);

                //                XTestCompositeNode node = (XTestCompositeNode) value;
//                String text = String.format(SPAN_FORMAT, "blue", value1.getUserObject());
//                this.setText("<html>" + text + "</html>");
            }
            return this;
        }
    }
}
