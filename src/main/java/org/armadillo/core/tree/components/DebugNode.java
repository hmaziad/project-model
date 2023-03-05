package org.armadillo.core.tree.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.jetbrains.annotations.VisibleForTesting;

import com.google.gson.annotations.Expose;
import com.intellij.ui.IconTestUtil;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.SimpleColoredText;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.impl.ui.tree.nodes.XDebuggerTreeNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@SuppressWarnings("ALL")
@Getter
@NoArgsConstructor(force = true)
@Log4j2
public class DebugNode extends DefaultMutableTreeNode {

    @Expose
    private List<String> texts = new ArrayList<>();
    @Expose
    private List<Integer> colors = new ArrayList<>();
    @Expose
    private String iconPath;

    @VisibleForTesting
    @Setter
    @Expose
    private List<DebugNode> myChildren = new ArrayList<>();

    @Setter
    private DebugColor color;

    @VisibleForTesting
    public DebugNode(String text) {

    }

    // exclude Mark
    public DebugNode(XDebuggerTreeNode xNode) {
        this.iconPath = getIconPath(xNode.getIcon()).orElse(null);
        SimpleColoredText simpleColoredText = xNode.getText();
        ArrayList<String> texts = simpleColoredText.getTexts();
        ArrayList<SimpleTextAttributes> attributes = simpleColoredText.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            this.texts.add(texts.get(i));
            Color fgColor = attributes.get(i).getFgColor();
            Integer rgb = Objects.isNull(fgColor) ? null : fgColor.getRGB();
            colors.add(rgb);
        }

        try {
            this.iconPath = IconTestUtil.getIconPath(xNode.getIcon());
        } catch (Exception e) {
            LOG.error("Could not get icon path: {}", e.toString());
        }

        List<? extends XDebuggerTreeNode> children = xNode.getLoadedChildren();
        children.forEach(child -> {
            DebugNode debugNode = new DebugNode(child);
            add(debugNode);
        });
    }

    public DebugNode(DebugNode value) {
        this.texts = value.getTexts();
        this.colors = value.getColors();
        this.iconPath = value.getIconPath();
        var children = value.getMyChildren();
        children.forEach(child -> {
            DebugNode debugNode = new DebugNode(child);
            add(debugNode);
        });
    }

    private Optional<String> getIconPath(Icon icon) {
        if (icon instanceof LayeredIcon) {
            var layeredIcon = (LayeredIcon) icon;
            for (var currentIcon : layeredIcon.getAllLayers()) {
                Optional<String> path = getIconPath(currentIcon);
                if (path.isPresent() && !path.get().contains("Mark")) {
                    return path;
                }
            }
        } else if(Objects.nonNull(icon)) {
            try {
                var input = icon.toString();
                int start = input.indexOf("path='") + 6;
                int end = input.indexOf("'", start);
                if (start == -1) {
                    start = input.indexOf("path=") + 5;
                    end = input.indexOf(")", start);
                }
                String path = input.substring(start, end);
                if (path != null && !path.isBlank()) {
                    return Optional.of(path);
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        LOG.error("Could not get Icon path for icon class {}", icon);
        return Optional.empty();
    }

    public void add(DebugNode newChild) {
        this.myChildren.add(newChild);
        add((MutableTreeNode) newChild);
    }

    @Override
    public String toString() {
        return String.join("", this.texts);
    }
}
