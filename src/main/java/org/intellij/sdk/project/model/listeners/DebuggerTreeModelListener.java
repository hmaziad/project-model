package org.intellij.sdk.project.model.listeners;

import static org.intellij.sdk.project.model.constants.TextConstants.DEBUGGER_SNAP_TAKEN;
import static org.intellij.sdk.project.model.constants.TextConstants.TAKE_DEBUGGER_SNAP;

import java.util.Objects;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;

public class DebuggerTreeModelListener implements TreeModelListener {
    private JLabel feedbackLabel;
    private JButton clearButton;

    public DebuggerTreeModelListener(JLabel feedbackLabel, JButton clearButton) {
        this.feedbackLabel = feedbackLabel;
        this.clearButton = clearButton;
    }

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
        boolean isRootNull = Objects.isNull(((DefaultTreeModel) e.getSource()).getRoot());
        if (isRootNull) {
            this.feedbackLabel.setText(TAKE_DEBUGGER_SNAP);
            this.clearButton.setEnabled(false);
        } else {
            this.feedbackLabel.setText(DEBUGGER_SNAP_TAKEN);
            this.clearButton.setEnabled(true);
        }
    }
}
