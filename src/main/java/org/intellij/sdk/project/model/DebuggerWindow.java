// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import static org.intellij.sdk.project.model.components.ButtonHandler.handleButton;
import static org.intellij.sdk.project.model.components.ButtonHandler.handleToolbar;
import static org.intellij.sdk.project.model.components.ButtonHandler.handleToolbarSeperator;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonType;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DebuggerWindow {

    @Getter
    private JPanel DebuggerWindowContent;
    private JPanel rightSideBar;
    private JButton snapButton;
    private JButton saveButton;
    private JButton diffButton;
    private JLayeredPane layeredPane;
    private JScrollPane treePane;
    private JTree debugTree;
    private JToolBar toolbar;
    private JToolBar.Separator toolbarSeparatorCore;
    private JButton clearButton;
    private JButton expandButton;
    private JButton collapseButton;
    private JToolBar.Separator toolbarSeparatorOther;
    private final JLabel noModelLabel = new JLabel("No session selected");


    /**
     * todo next
     * use Jlayered Pane for label and tree pane
     * maybe jpane splitter for right side bar
     */

    public DebuggerWindow() {
        handleButton(this.snapButton, ButtonType.SNAP);
        handleButton(this.saveButton, ButtonType.SAVE);
        handleButton(this.diffButton, ButtonType.DIFF);
        handleButton(this.clearButton, ButtonType.CLEAR);
        handleButton(this.expandButton, ButtonType.EXPAND);
        handleButton(this.collapseButton, ButtonType.COLLAPSE);
        handleToolbarSeperator(this.toolbarSeparatorCore);
        handleToolbar(this.toolbar);

        this.debugTree.setRootVisible(false);
        this.treePane.add(this.noModelLabel);
        this.debugTree.setModel(new DefaultTreeModel(null));
        this.debugTree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                noModelLabel.setVisible(e.getChildren().length == 0);
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {

            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {

            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {

            }
        });

    }

}