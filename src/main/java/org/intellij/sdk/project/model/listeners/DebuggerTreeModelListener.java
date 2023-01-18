package org.intellij.sdk.project.model.listeners;

import java.util.Objects;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.handlers.ReachServices;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DebuggerTreeModelListener implements TreeModelListener, ReachServices {
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
            COMPONENT_SERVICE.getClearIsEnabled().set(false);
        } else {
            COMPONENT_SERVICE.getClearIsEnabled().set(true);
        }
    }
}
