// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonHandler;
import org.intellij.sdk.project.model.components.ButtonType;
import org.intellij.sdk.project.model.components.ClearHandler;
import org.intellij.sdk.project.model.components.CollapseTreeHandler;
import org.intellij.sdk.project.model.components.DeleteHandler;
import org.intellij.sdk.project.model.components.DiffHandler;
import org.intellij.sdk.project.model.components.DiffRefHandler;
import org.intellij.sdk.project.model.components.DropdownHandler;
import org.intellij.sdk.project.model.components.DropdownObserver;
import org.intellij.sdk.project.model.components.ExpandTreeHandler;
import org.intellij.sdk.project.model.components.SaveHandler;
import org.intellij.sdk.project.model.components.SavedNodesView;
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
    private JToolBar.Separator toolbarSeparatorFeedback;
    private JLabel feedbackLabel;
    private JComboBox<String> savedSnapsDropdown;
    private JButton deleteButton;
    private JToolBar.Separator toolbarSeparatorSnap;
    private JLabel savedNodesLabel;
    private JComboBox<String> refSavedSnapsDropdown;
    private JButton diffRefButton;
    private JLabel diffSavedNodesLabel;
    private JLabel refSavedNodesLabel;
    private JButton viewSavedNodesButton;
    private JToolBar.Separator toolbarSeparatorViewNodes;

    public DebuggerWindow(@NotNull Project project) {
        this.feedbackLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        DefaultTreeModel treeModel = (DefaultTreeModel) this.debugTree.getModel();
        treeModel.setRoot(null);

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
        buttonHandler.handleButton(this.diffRefButton, ButtonType.DIFF);
        buttonHandler.handleButton(this.viewSavedNodesButton, ButtonType.VIEW_NODES);

        // toolbar separators
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorCore);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorOther);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorFeedback);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorSnap);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorViewNodes);

        // components
        DropdownObserver savedDropdownObserver = new DropdownObserver(this.savedSnapsDropdown);
        DropdownObserver refDropdownObserver = new DropdownObserver(this.refSavedSnapsDropdown);
        SnapHandler snapHandler = new SnapHandler(project, this.feedbackLabel, treeModel::setRoot);
        ToolHandler clearHandler = new ClearHandler(this.feedbackLabel);
        ToolHandler saveHandler = new SaveHandler(this.feedbackLabel, savedDropdownObserver, refDropdownObserver);
        ToolHandler deleteHandler = new DeleteHandler(this.feedbackLabel, savedDropdownObserver, refDropdownObserver, project);
        ToolHandler diffHandler = new DiffHandler(this.feedbackLabel, project);
        ToolHandler diffRefHandler = new DiffRefHandler(this.feedbackLabel, project, savedDropdownObserver, refDropdownObserver);
        ToolHandler savedDropDownHandler = new DropdownHandler(this.feedbackLabel, savedDropdownObserver);
        ToolHandler expandTreeHandler = new ExpandTreeHandler(debugTree);
        ToolHandler collapseTreeHandler = new CollapseTreeHandler(debugTree);

        this.debugTree.setRootVisible(false);
        this.debugTree.setCellRenderer(new DebuggerTreeRenderer());
        this.debugTree
            .getModel()
            .addTreeModelListener(new DebuggerTreeModelListener(this.feedbackLabel, buttonHandler.getAllButtons(), expandTreeHandler));

        // action listeners
        this.clearButton.addActionListener(e -> clearHandler.handle(treeModel));
        this.snapButton.addActionListener(e -> snapHandler.handle(treeModel));
        this.saveButton.addActionListener(e -> saveHandler.handle(treeModel));
        this.deleteButton.addActionListener(e -> deleteHandler.handle(treeModel));
        this.diffButton.addActionListener(e -> diffHandler.handle(treeModel));
        this.expandButton.addActionListener(e -> expandTreeHandler.handle(treeModel));
        this.collapseButton.addActionListener(e -> collapseTreeHandler.handle(treeModel));
        this.savedSnapsDropdown.addActionListener(e -> savedDropDownHandler.handle(treeModel));
        this.diffRefButton.addActionListener(e -> diffRefHandler.handle(treeModel));
        this.viewSavedNodesButton.addActionListener(e -> new SavedNodesView().showAndGet());

    }

}