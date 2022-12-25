package org.intellij.sdk.project.model.services;

import java.util.List;

import javax.swing.*;
import org.intellij.sdk.project.model.xnodes.DebugNode;

public class NodeNavigatorService {
    private final List<DebugNode> diffNodes;
    private final JTree nodeTree;
    private int index = -1;
    private List<DebugNode> groupedDiffNodes;

    public NodeNavigatorService(List<DebugNode> diffNodes, JTree nodeTree) {
        this.diffNodes = diffNodes;
        this.nodeTree = nodeTree;
        reset();
    }

    public void navigateDown() {
        if (this.groupedDiffNodes.isEmpty() || this.index == this.groupedDiffNodes.size() - 1) {
            return;
        }
        this.index++;
        updateTreeSelectionAndScroll();
    }

    public void navigateUp() {
        if (this.groupedDiffNodes.isEmpty() || this.index == 0) {
            return;
        }
        this.index--;
        updateTreeSelectionAndScroll();
    }

    private void updateTreeSelectionAndScroll() {
//        TreePath nodePath = new TreePath(this.diffNodes.get(this.groupedDiffNodes.get(this.index).getLineNumber()).getPath());
//        this.nodeTree.expandPath(nodePath);
//        this.nodeTree.setSelectionPath(nodePath);
//        Rectangle bounds = nodeTree.getPathBounds(nodePath);
//        if (Objects.nonNull(bounds)) {
//            bounds.height = nodeTree.getVisibleRect().height;
//            nodeTree.scrollRectToVisible(bounds);
//        }
    }

    public void reset() {
        this.index = -1;
    }
}
