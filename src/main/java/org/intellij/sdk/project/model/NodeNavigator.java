package org.intellij.sdk.project.model;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.TreePath;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;

public class NodeNavigator {
    private final List<XTestCompositeNode> diffNodes;
    private final JTree nodeTree;
    private int index = -1;

    public NodeNavigator(List<XTestCompositeNode> diffNodes, JTree nodeTree) {
        this.diffNodes = diffNodes;
        this.nodeTree = nodeTree;
    }

    public void navigateDown() {
        if (diffNodes.isEmpty() || this.index == diffNodes.size() - 1) {
            return;
        }
        this.index++;
        updateTreeSelectionAndScroll();
    }

    public void navigateUp() {
        if (diffNodes.isEmpty() || this.index == 0) {
            return;
        }
        this.index--;
        updateTreeSelectionAndScroll();
    }

    private void updateTreeSelectionAndScroll() {
        TreePath nodePath = new TreePath(diffNodes.get(this.index).getPath());
        this.nodeTree.expandPath(nodePath);
        this.nodeTree.setSelectionPath(nodePath);
        Rectangle bounds = nodeTree.getPathBounds(nodePath);
        if (Objects.nonNull(bounds)) {
            bounds.height = nodeTree.getVisibleRect().height;
            nodeTree.scrollRectToVisible(bounds);
        }
    }

    public void reset() {
        this.index = -1;
    }
}
