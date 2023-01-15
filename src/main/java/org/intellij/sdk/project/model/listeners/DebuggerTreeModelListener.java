package org.intellij.sdk.project.model.listeners;

import static org.intellij.sdk.project.model.constants.TextConstants.DEBUGGER_SNAP_TAKEN;

import java.util.Objects;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.handlers.ToolHandler;
import org.intellij.sdk.project.model.services.ButtonEnablingService;
import com.intellij.openapi.components.ServiceManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DebuggerTreeModelListener implements TreeModelListener {
    private static final ButtonEnablingService buttonEnablingService = ServiceManager.getService(ButtonEnablingService.class);
    private final JLabel feedbackLabel;

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        DefaultTreeModel treeModel = (DefaultTreeModel) e.getSource();
        boolean isRootNull = Objects.isNull(treeModel.getRoot());
        if (isRootNull) {
            buttonEnablingService.setClearButtonEnabled(false);
        } else {
            buttonEnablingService.setClearButtonEnabled(true);
            this.feedbackLabel.setText(DEBUGGER_SNAP_TAKEN);
        }
    }
}
