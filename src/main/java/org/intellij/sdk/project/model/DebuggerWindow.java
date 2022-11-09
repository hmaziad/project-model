// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import static org.intellij.sdk.project.model.components.ButtonHandler.handleButton;
import static org.intellij.sdk.project.model.components.ButtonHandler.handleToolbar;
import static org.intellij.sdk.project.model.components.ButtonHandler.handleToolbarSeperator;
import static org.intellij.sdk.project.model.constants.TextConstants.TAKE_DEBUGGER_SNAP;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonType;
import org.intellij.sdk.project.model.listeners.DebuggerTreeModelListener;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DebuggerWindow {

    @Getter
    private JPanel debuggerWindowContent;
    private JPanel rightSideBar;
    private JButton snapButton;
    private JButton saveButton;
    private JButton diffButton;
    private JScrollPane treePane;
    private JTree debugTree;
    private JToolBar toolbar;
    private JToolBar.Separator toolbarSeparatorCore;
    private JButton clearButton;
    private JButton expandButton;
    private JButton collapseButton;
    private JToolBar.Separator toolbarSeparatorOther;
    private JButton previousButton;
    private JButton nextButton;
    private JButton testButton;
    private JLabel feedbackLabel;
    private boolean toggleNode;

    public DebuggerWindow() {
        // toolbar
        handleToolbar(this.toolbar);

        // icon buttons in toolbar
        handleButton(this.snapButton, ButtonType.SNAP);
        handleButton(this.saveButton, ButtonType.SAVE);
        handleButton(this.diffButton, ButtonType.DIFF);
        handleButton(this.clearButton, ButtonType.CLEAR);
        handleButton(this.expandButton, ButtonType.EXPAND);
        handleButton(this.collapseButton, ButtonType.COLLAPSE);
        handleButton(this.previousButton, ButtonType.PREVIOUS);
        handleButton(this.nextButton, ButtonType.NEXT);

        // toolbar separators
        handleToolbarSeperator(this.toolbarSeparatorCore);
        handleToolbarSeperator(this.toolbarSeparatorOther);

        // initialize
        this.debugTree.setRootVisible(true);
        this.feedbackLabel.setText(TAKE_DEBUGGER_SNAP);
        this.debugTree.getModel().addTreeModelListener(new DebuggerTreeModelListener(this.feedbackLabel));

        // test button to be removed eventually
        testButton.addActionListener(e -> {
            DefaultTreeModel treeModel = (DefaultTreeModel) this.debugTree.getModel();
            if (toggleNode) {
                treeModel.setRoot(null);
                toggleNode = false;
            } else {
                treeModel.setRoot(new DefaultMutableTreeNode("hello"));
                toggleNode = true;
            }
        });

    }

}