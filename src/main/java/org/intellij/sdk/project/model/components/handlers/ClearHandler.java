package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.CLEARED_SNAP_MESSAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.WINDOW_IS_ALREADY_CLEAR;

import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class ClearHandler {
    private final JLabel feedbackLabel;

    public void handle(DefaultTreeModel treeModel) {
        if (Objects.isNull(treeModel.getRoot())) {
            this.feedbackLabel.setText(WINDOW_IS_ALREADY_CLEAR);
            return;
        }
        treeModel.setRoot(null);
        this.feedbackLabel.setText(CLEARED_SNAP_MESSAGE);
    }
}
