package org.armadillo.core.tree.components;

import static org.armadillo.core.tree.components.DebugColor.BLUE;
import static org.armadillo.core.tree.components.DebugColor.GREEN;
import static org.armadillo.core.tree.components.DebugColor.RED;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;

import lombok.Getter;

public class DebugTreeManager {
    @Getter
    private final JTree debugTree = new Tree();
    private Project project;

    public DebugTreeManager(boolean allowColors) {
        this.debugTree.setRootVisible(false);
        this.debugTree.setCellRenderer(new DebugTreeRenderer(allowColors));
        final TreePopup treePopup = new TreePopup(this.debugTree);
        this.debugTree.addMouseListener(getMouseAdapter(treePopup));
        new TreeSpeedSearch(this.debugTree);
    }

    public void addClearButtonListener() {
        this.debugTree.getModel().addTreeModelListener(new DebuggerTreeModelListener(this.project));
    }

    public void setRoot(DebugNode debugNode) {
        ((DefaultTreeModel) this.debugTree.getModel()).setRoot(debugNode);
        expandAll(this.debugTree);
    }

    private void expandAll(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    @NotNull
    private MouseAdapter getMouseAdapter(TreePopup treePopup) {
        return new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = debugTree.getRowForLocation(e.getX(), e.getY());
                    debugTree.setSelectionRow(row);
                    if (row >= 0) {
                        treePopup.setRow(row);
                        treePopup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        };
    }

    public void addDiffInsertions(List<List<Integer>> additions) {
        for (List<Integer> range : additions) {
            int start = range.get(0);
            int end = range.get(1);
            for (int row = start; row < end; row++) {
                TreePath path = this.debugTree.getPathForRow(row);
                DebugNode debugNode = (DebugNode) path.getLastPathComponent();
                debugNode.setColor(GREEN);
            }
        }
    }

    public void addDiffDeletions(List<List<Integer>> deletions) {
        for (List<Integer> range : deletions) {
            int start = range.get(0);
            int end = range.get(1);
            for (int row = start; row < end; row++) {
                TreePath path = this.debugTree.getPathForRow(row);
                DebugNode debugNode = (DebugNode) path.getLastPathComponent();
                debugNode.setColor(RED);
            }
        }
    }

    public void addDiffModifications(List<List<Integer>> modifications) {
        for (List<Integer> range : modifications) {
            int start = range.get(0);
            int end = range.get(1);
            int rowCount = this.debugTree.getRowCount();
            for (int row = start; row < Math.min(end, rowCount); row++) {
                TreePath path = this.debugTree.getPathForRow(row);
                DebugNode debugNode = (DebugNode) path.getLastPathComponent();
                debugNode.setColor(BLUE);
            }
        }
    }

    public void clearNodeColors() {
        DebugNode rootNode = (DebugNode) this.debugTree.getModel().getRoot();
        clearNodeColors(rootNode);
    }

    private void clearNodeColors(DebugNode rootNode) {
        rootNode.setColor(null);
        for (DebugNode child : rootNode.getMyChildren()) {
            clearNodeColors(child);
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }

    private class TreePopup extends JPopupMenu {
        private int row;

        public TreePopup(JTree tree) {
            JMenuItem expand = new JMenuItem("Expand");
            JMenuItem collapse = new JMenuItem("Collapse");
            JMenuItem expandAll = new JMenuItem("Expand All");
            JMenuItem collapseAll = new JMenuItem("Collapse All");
            expand.addActionListener(ae -> expandCollapse(tree, tree.getPathForRow(row), true));
            collapse.addActionListener(ae -> expandCollapse(tree, tree.getPathForRow(row), false));
            expandAll.addActionListener(ae -> expandAll(tree));
            collapseAll.addActionListener(ae -> {
                for (int i = tree.getRowCount() - 1; i >= 0; i--) {
                    tree.collapseRow(i);
                }
            });

            add(expand);
            add(collapse);
            add(new JSeparator());
            add(expandAll);
            add(collapseAll);
        }

        public void setRow(int row) {
            this.row = row;
        }

        private void expandCollapse(JTree tree, TreePath path, boolean expand) {
            TreeNode node = (TreeNode) path.getLastPathComponent();

            if (node.getChildCount() >= 0) {
                Enumeration enumeration = node.children();
                while (enumeration.hasMoreElements()) {
                    TreeNode n = (TreeNode) enumeration.nextElement();
                    TreePath p = path.pathByAddingChild(n);

                    expandCollapse(tree, p, expand);
                }
            }

            if (expand) {
                tree.expandPath(path);
            } else {
                tree.collapsePath(path);
            }
        }
    }

}
