package org.intellij.sdk.project.model;


import java.awt.*;
import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.util.IconLoader;

public class DebuggerTreeRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DebugNode node = (DebugNode) value;
        node.isLeaf();
        if (Objects.nonNull(node.getIconPath())) {
            setIcon(IconLoader.getIcon(node.getIconPath(), DebuggerTreeRenderer.class));
        }
        return this;
    }
}