package org.armadillo.core.tree.components;

import java.util.Objects;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import org.armadillo.core.components.handlers.ReachServices;
import com.intellij.openapi.project.Project;

public class DebuggerTreeModelListener implements TreeModelListener, ReachServices {

    private final Project project;

    public DebuggerTreeModelListener(Project project) {
        this.project = project;
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
        DefaultTreeModel treeModel = (DefaultTreeModel) e.getSource();
        boolean isRootNull = Objects.isNull(treeModel.getRoot());
        if (isRootNull) {
            treeHandler.setClearEnabled(false, this.project);
        } else {
            treeHandler.setClearEnabled(true, this.project);
        }
    }
}
