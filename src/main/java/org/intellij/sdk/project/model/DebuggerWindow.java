// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonHandler;
import org.intellij.sdk.project.model.components.ButtonType;
import org.intellij.sdk.project.model.components.ClearHandler;
import org.intellij.sdk.project.model.components.DeleteHandler;
import org.intellij.sdk.project.model.components.DiffHandler;
import org.intellij.sdk.project.model.components.DropdownHandler;
import org.intellij.sdk.project.model.components.DropdownObserver;
import org.intellij.sdk.project.model.components.SaveHandler;
import org.intellij.sdk.project.model.components.SnapHandler;
import org.intellij.sdk.project.model.components.ToolHandler;
import org.intellij.sdk.project.model.listeners.DebuggerTreeModelListener;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;

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
    private JToolBar.Separator toolbarSeparatorFeedback;
    private JLabel feedbackLabel;
    private JComboBox<String> savedSnapsDropdown;
    private JButton deleteButton;
    private JToolBar.Separator toolbarSeparatorSnap;
    private boolean toggleNode;

    public DebuggerWindow(@NotNull Project project) {
        ButtonHandler buttonHandler = new ButtonHandler();
        // toolbar
        buttonHandler.handleToolbar(this.toolbar);

        // icon buttons in toolbar
        buttonHandler.handleButton(this.snapButton, ButtonType.SNAP);
        buttonHandler.handleButton(this.saveButton, ButtonType.SAVE);
        buttonHandler.handleButton(this.diffButton, ButtonType.DIFF);
        buttonHandler.handleButton(this.clearButton, ButtonType.CLEAR);
        buttonHandler.handleButton(this.deleteButton, ButtonType.DELETE);
        buttonHandler.handleButton(this.expandButton, ButtonType.EXPAND);
        buttonHandler.handleButton(this.collapseButton, ButtonType.COLLAPSE);
        buttonHandler.handleButton(this.previousButton, ButtonType.PREVIOUS);
        buttonHandler.handleButton(this.nextButton, ButtonType.NEXT);

        // toolbar separators
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorCore);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorOther);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorFeedback);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorSnap);

        this.debugTree.setRootVisible(false);
        this.debugTree
            .getModel()
            .addTreeModelListener(new DebuggerTreeModelListener(this.feedbackLabel, buttonHandler.getAllButtons()));
        this.debugTree.setCellRenderer(new DebuggerTreeRenderer());

        DefaultTreeModel treeModel = (DefaultTreeModel) this.debugTree.getModel();
        treeModel.setRoot(null);

        // components
        DropdownObserver dropdownObserver = new DropdownObserver(this.savedSnapsDropdown);
        SnapHandler snapHandler = new SnapHandler(project, this.feedbackLabel, treeModel::setRoot);
        ToolHandler clearHandler = new ClearHandler(this.feedbackLabel);
        ToolHandler saveHandler = new SaveHandler(this.feedbackLabel, dropdownObserver, project);
        ToolHandler deleteHandler = new DeleteHandler(this.feedbackLabel, dropdownObserver, project);
        ToolHandler diffHandler = new DiffHandler(this.feedbackLabel, project);
        ToolHandler dropDownHandler = new DropdownHandler(this.feedbackLabel, dropdownObserver);

        this.clearButton.addActionListener(e -> clearHandler.handle(treeModel));
        this.snapButton.addActionListener(e -> snapHandler.handle(treeModel));
        this.saveButton.addActionListener(e -> saveHandler.handle(treeModel));
        this.deleteButton.addActionListener(e -> deleteHandler.handle(treeModel));
        this.diffButton.addActionListener(e -> diffHandler.handle(treeModel));
        this.savedSnapsDropdown.addActionListener(e -> dropDownHandler.handle(treeModel));

        // test button to be removed eventually
        this.testButton.setVisible(false);
        this.toolbarSeparatorFeedback.setVisible(false);
        this.testButton.addActionListener(e -> {
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