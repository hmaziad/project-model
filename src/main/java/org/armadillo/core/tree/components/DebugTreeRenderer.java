package org.armadillo.core.tree.components;


import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import com.intellij.openapi.util.IconLoader;

public class DebugTreeRenderer extends DefaultTreeCellRenderer {
    private static final String HTML_FORMAT = "<html>%s</html>";
    private static final String SPAN_FORMAT = "<span style=\"color:%s;\">%s</span>";
    private Color theColor = null;
    private final boolean allowColors;
    private static final Map<Character, String> specialCharacters = new HashMap<>();

    static {
        specialCharacters.put('&', "&amp;");
        specialCharacters.put('>', "&gt;");
        specialCharacters.put('<', "&lt;");
        specialCharacters.put('"', "&quot;");
        specialCharacters.put('\'', "&#039;");
    }

    public DebugTreeRenderer(boolean allowColors) {
        this.allowColors = allowColors;
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
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
        if (!(value instanceof DebugNode)) {
            return this;
        }
        DebugNode node = (DebugNode) value;
        StringBuilder sb = new StringBuilder();

        List<String> texts = node.getTexts();
        List<Integer> colors = node.getColors();

        for (int i = 0; i < texts.size(); i++) {
            Integer rgb = colors.get(i);
            Color color = Objects.isNull(rgb) ? Color.lightGray : new Color(rgb);
            String text = texts.get(i);
            String hexColor = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            text = parseSpecialCharacters(text);
            sb.append(String.format(SPAN_FORMAT, hexColor, text));
        }
        setText(String.format(HTML_FORMAT, sb));

        if (Objects.nonNull(node.getColor()) && this.allowColors) {
            this.theColor = node.getColor().getRealColor();
        } else {
            this.theColor = null;
        }

        if (Objects.nonNull(node.getIconPath())) {
            setIcon(IconLoader.getIcon(node.getIconPath(), DebugTreeRenderer.class));
        }
        return this;
    }

    private String parseSpecialCharacters(String text) {
        StringBuilder textBuilder = new StringBuilder();
        for (char ch : text.toCharArray()) {
            textBuilder.append(specialCharacters.getOrDefault(ch, String.valueOf(ch)));
        }
        return textBuilder.toString();
    }
}