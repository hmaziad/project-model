package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SNAP_MESSAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_THE_SAVED_NODE_Q;
import static org.intellij.sdk.project.model.constants.TextConstants.NO_SAVED_NODES;

import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import com.intellij.openapi.project.Project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class DeleteHandler implements ToolHandler {
    private final JLabel feedbackLabel;
    private final DropdownObserver dropdownObserver;
    private final Project project;

    public void handle(DefaultTreeModel treeModel) {
        if (Objects.isNull(this.dropdownObserver.getCurrentItem())) {
            this.feedbackLabel.setText(NO_SAVED_NODES);
            return;
        }
        String message = String.format(DELETE_THE_SAVED_NODE_Q, this.dropdownObserver.getCurrentItem());
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, "Delete Snap", this.project);
        if (isSure) {
            this.dropdownObserver.removeCurrentItem();
            treeModel.setRoot(null);
            this.feedbackLabel.setText(DELETE_SNAP_MESSAGE);
        }
    }
}
