package org.intellij.sdk.project.model.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;

public class DebuggerTreeRenderer extends DefaultTreeCellRenderer {
    // Both methods are needed
    private Color theColor;

    @Override
    public Color getBackground() {
        return theColor;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return theColor;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        XTestCompositeNode node = (XTestCompositeNode) value;
        setIcon(node.getIcon());
        if (node.getDiffChar() == '+') {
            this.theColor = Color.decode("#2E5A44"); // red
        } else if (node.getDiffChar() == '-') {
            this.theColor = Color.decode("#6B3031"); // green
        } else {
            this.theColor = null;
        }
        return this;
    }
}