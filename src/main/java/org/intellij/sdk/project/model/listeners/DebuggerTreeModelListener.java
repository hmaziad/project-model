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

    public DebuggerTreeModelListener(JLabel feedbackLabel) {
        this.feedbackLabel = feedbackLabel;
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
        this.feedbackLabel.setText(isRootNull ? TAKE_DEBUGGER_SNAP : DEBUGGER_SNAP_TAKEN);
    }
}
