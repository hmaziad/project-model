// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonType;
import org.intellij.sdk.project.model.components.handlers.ButtonHandler;
import org.intellij.sdk.project.model.components.handlers.ClearHandler;
import org.intellij.sdk.project.model.components.handlers.CollapseTreeHandler;
import org.intellij.sdk.project.model.components.handlers.DeleteHandler;
import org.intellij.sdk.project.model.components.handlers.DiffHandler;
import org.intellij.sdk.project.model.components.handlers.ExpandTreeHandler;
import org.intellij.sdk.project.model.components.handlers.SaveHandler;
import org.intellij.sdk.project.model.components.handlers.SnapHandler;
import org.intellij.sdk.project.model.components.handlers.ToolHandler;
import org.intellij.sdk.project.model.components.views.DiffNodesView;
import org.intellij.sdk.project.model.components.views.SettingsNodesView;
import org.intellij.sdk.project.model.components.views.UploadNodesView;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.listeners.DebuggerTreeModelListener;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DebuggerWindow {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);

    @Getter
    private JPanel debuggerWindowContent;
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
    private JToolBar.Separator toolbarSeparatorSnap;
    private JButton viewSavedNodesButton;
    private JToolBar.Separator toolbarSeparatorViewNodes;
    private JButton diffSavedButton;
    private JButton uploadButton;

    public DebuggerWindow(@NotNull Project project) {
        JButton diffRefButton = new JButton();
        this.feedbackLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        DefaultTreeModel treeModel = (DefaultTreeModel) this.debugTree.getModel();
        treeModel.setRoot(null);

        ButtonHandler buttonHandler = new ButtonHandler();
        // toolbar
        buttonHandler.handleToolbar(this.toolbar);

        // icon buttons in toolbar
        buttonHandler.handleButton(this.snapButton, ButtonType.SNAP);
        buttonHandler.handleButton(this.saveButton, ButtonType.SAVE);
        buttonHandler.handleButton(this.uploadButton, ButtonType.UPLOAD);
        buttonHandler.handleButton(this.diffButton, ButtonType.DIFF);
        buttonHandler.handleButton(this.clearButton, ButtonType.CLEAR);
        buttonHandler.handleButton(this.expandButton, ButtonType.EXPAND);
        buttonHandler.handleButton(this.collapseButton, ButtonType.COLLAPSE);
        buttonHandler.handleButton(diffRefButton, ButtonType.DIFF_SCALED);
        buttonHandler.handleButton(this.diffSavedButton, ButtonType.DIFF_SAVED);
        buttonHandler.handleButton(this.viewSavedNodesButton, ButtonType.VIEW_NODES);

        // toolbar separators
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorCore);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorOther);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorFeedback);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorSnap);
        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorViewNodes);

        // components

        SnapHandler snapHandler = new SnapHandler(project, this.feedbackLabel, treeModel::setRoot);
        ToolHandler clearHandler = new ClearHandler(this.feedbackLabel);
        SaveHandler saveHandler = new SaveHandler(this.feedbackLabel);
        DeleteHandler deleteHandler = new DeleteHandler(this.feedbackLabel, project);
        ToolHandler diffHandler = new DiffHandler(this.feedbackLabel, project);
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
        this.diffButton.addActionListener(e -> diffHandler.handle(treeModel));
        this.expandButton.addActionListener(e -> expandTreeHandler.handle(treeModel));
        this.collapseButton.addActionListener(e -> collapseTreeHandler.handle(treeModel));
        this.viewSavedNodesButton.addActionListener(e -> new SettingsNodesView(project,saveHandler, deleteHandler, treeModel).showAndGet());
        this.diffSavedButton.addActionListener(e -> {
            if (persistencyService.getNodes().size() == 0) {
                MessageDialogues.getErrorMessageDialogue("You haven't saved any nodes yet", project);
            } else {
                new DiffNodesView(project, diffRefButton).showAndGet();
            }
        });
        this.uploadButton.addActionListener(e -> new UploadNodesView(project, treeModel).showAndGet());

    }

}