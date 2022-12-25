package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.RETRIEVED_NODE_FROM_STORAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.SELECTED_LABEL_IS_NULL;
import static org.intellij.sdk.project.model.constants.TextConstants.SELECTED_NODE_IS_NULL;

import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.ServiceManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DropdownHandler implements ToolHandler {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private final JLabel feedbackLabel;
    private final DropdownObserver dropdownObserver;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        String selectedItemLabel = dropdownObserver.getCurrentItem();
        if (Objects.isNull(selectedItemLabel)) {
            this.feedbackLabel.setText(SELECTED_LABEL_IS_NULL);
        }
        DebugNode selectedNode = persistencyService.getNodes().get(selectedItemLabel);
        if (Objects.isNull(selectedNode)) {
            this.feedbackLabel.setText(SELECTED_NODE_IS_NULL);
        }
        treeModel.setRoot(selectedNode);
        this.feedbackLabel.setText(String.format(RETRIEVED_NODE_FROM_STORAGE, selectedItemLabel));

    }
}
