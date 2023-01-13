package org.intellij.sdk.project.model;


import java.util.Objects;

import javax.swing.*;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleColoredText;

public class DebuggerTreeRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DebugNode node = (DebugNode) value;
        node.isLeaf();
        final SimpleColoredText text = node.getColoredText();
        text.appendToComponent(this);
        if (Objects.nonNull(node.getIconPath())) {
            setIcon(IconLoader.getIcon(node.getIconPath(), DebuggerTreeRenderer.class));
        }
    }
}