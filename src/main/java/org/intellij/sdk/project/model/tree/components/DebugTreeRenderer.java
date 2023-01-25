package org.intellij.sdk.project.model.tree.components;


import java.awt.*;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;

public class DebugTreeRenderer extends DefaultTreeCellRenderer {
    private static final String HTML_FORMAT = "<html>%s</html>";
    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";
    private JBColor theColor = null;
    private String text;
    private Icon icon;

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Color getBackground() {
        return theColor;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return theColor;
    }

    @Override
    public Icon getIcon() {
        return this.icon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        DebugNode node = (DebugNode) value;
        StringBuilder sb = new StringBuilder();

        List<String> texts = node.getTexts();
        List<Integer> colors = node.getColors();

        for (int i = 0; i < texts.size(); i++) {
            Integer rgb = colors.get(i);
            Color color = Objects.isNull(rgb) ? Color.lightGray : new Color(rgb);
            String text = texts.get(i);
            String hexColor = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            sb.append(String.format(SPAN_FORMAT, hexColor, text));
        }
        this.text = String.format(HTML_FORMAT, sb);

        if (Objects.nonNull(node.getIconPath())) {
            this.icon = IconLoader.getIcon(node.getIconPath(), DebugTreeRenderer.class);
        }
        return this;
    }
}

/*
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
    }*/
