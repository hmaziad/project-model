package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETED_SNAP_MESSAGE;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class DeleteHandler implements ToolHandler {
    private final JLabel feedbackLabel;

    public void handle(DefaultTreeModel treeModel) {
        treeModel.setRoot(null);
        this.feedbackLabel.setText(DELETED_SNAP_MESSAGE);
    }
}
