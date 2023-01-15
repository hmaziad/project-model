package org.intellij.sdk.project.model.xnodes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.jetbrains.annotations.VisibleForTesting;
import com.google.gson.annotations.Expose;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.SimpleColoredText;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueContainerNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
//@Log4j2
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

    @VisibleForTesting
    public DebugNode(String text) {

    }

    // exclude Mark
    public DebugNode(XValueContainerNode<?> xNode) {
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
            IconLoader.CachedImageIcon icon = (IconLoader.CachedImageIcon) xNode.getIcon();
            if (Objects.nonNull(icon)) {
                this.iconPath = icon.getOriginalPath(); // e.g. nodes/parameter.svg
            }
        } catch (Exception e) {
//            LOG.error("Could not get icon path: {}", e.toString());
        }

        List<? extends XValueContainerNode<?>> children = xNode.getLoadedChildren();
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
        } else if (icon instanceof IconLoader.CachedImageIcon) {
            IconLoader.CachedImageIcon imageIcon = (IconLoader.CachedImageIcon) icon;
            if (Objects.nonNull(imageIcon)) {
                return Optional.of(imageIcon.getOriginalPath()); // e.g. nodes/parameter.svg
            }
        }
//        LOG.error("Could not get Icon path for icon class {}", icon);
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
