package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SNAP_MESSAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_THE_SAVED_NODE_Q;
import static org.intellij.sdk.project.model.constants.TextConstants.NO_SAVED_NODES;

import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.services.PersistencyService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class DeleteHandler implements ToolHandler {
    private final JLabel feedbackLabel;
    private final DropdownObserver saveDropdownObserver;
    private final DropdownObserver refDropdownObserver;
    private final Project project;
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);

    public void handle(DefaultTreeModel treeModel) {
        String currentItem = this.saveDropdownObserver.getCurrentItem();
        delete(treeModel, currentItem);
    }

    public void handle(DefaultTreeModel treeModel, String nodeKeyName) {
        delete(treeModel, nodeKeyName);
    }

    private void delete(DefaultTreeModel treeModel, String nodeKeyName) {
        if (Objects.isNull(nodeKeyName)) {
            this.feedbackLabel.setText(NO_SAVED_NODES);
            return;
        }
        String message = String.format(DELETE_THE_SAVED_NODE_Q, nodeKeyName);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, "Delete Snap", this.project);
        if (isSure) {
            persistencyService.getNodes().remove(nodeKeyName);
            this.saveDropdownObserver.removeItem(nodeKeyName);
            this.refDropdownObserver.removeItem(nodeKeyName);
            if (this.saveDropdownObserver.getCurrentItem().equals(nodeKeyName)) {
                treeModel.setRoot(null);
            }
            this.feedbackLabel.setText(DELETE_SNAP_MESSAGE);
        }
    }
}
