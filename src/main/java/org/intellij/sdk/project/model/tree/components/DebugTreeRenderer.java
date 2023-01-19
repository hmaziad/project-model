package org.intellij.sdk.project.model.tree.components;


import static com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleColoredText;
import com.intellij.ui.SimpleTextAttributes;

public class DebugTreeRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (!(value instanceof DebugNode)) {
            return;
        }
        DebugNode node = (DebugNode) value;
        final SimpleColoredText simpleColoredText = new SimpleColoredText();
        List<String> texts = node.getTexts();
        List<Integer> colors = node.getColors();
        for (int i = 0; i < texts.size(); i++) {
            Integer rgb = colors.get(i);
            Color color = Objects.isNull(rgb) ? Color.lightGray : new Color(rgb);
            simpleColoredText.append(texts.get(i), new SimpleTextAttributes(STYLE_PLAIN, color));
        }
        simpleColoredText.appendToComponent(this);
        if (Objects.nonNull(node.getIconPath())) {
            setIcon(IconLoader.getIcon(node.getIconPath(), DebugTreeRenderer.class));
        }
    }
}