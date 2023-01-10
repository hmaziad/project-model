package org.intellij.sdk.project.model.components.handlers;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.*;

import org.intellij.sdk.project.model.components.ButtonType;

import icons.SdkIcons;
import lombok.Getter;

public class ButtonHandler {
    public static final Color grey = new Color(118, 124, 130);

    @Getter
    private final Map<ButtonType, JButton> allButtons = new EnumMap<>(ButtonType.class);

    public void handleButton(JButton button, ButtonType buttonType) {
        button.getModel() //
            .addChangeListener(e -> button.setContentAreaFilled(button.getModel().isRollover()));

        switch (buttonType) {
            case SNAP:
                button.setIcon(SdkIcons.SNAP_ICON);
                break;
            case DIFF:
                button.setIcon(SdkIcons.DIFF_ICON);
                break;
            case CLEAR:
                button.setIcon(SdkIcons.CLEAR_ICON);
                break;
            case EXPAND:
                button.setIcon(SdkIcons.EXPAND_ICON);
                break;
            case COLLAPSE:
                button.setIcon(SdkIcons.COLLAPSE_ICON);
                break;
            case VIEW_NODES:
                button.setIcon(SdkIcons.VIEW_NODES_ICON);
                break;
            case DIFF_SAVED:
                button.setIcon(SdkIcons.DIFF_SAVED);
                break;
            case DIFF_SCALED:
                button.setIcon(SdkIcons.DIFF_SCALED);
                break;
            case UPLOAD:
                button.setIcon(SdkIcons.UPLOAD);
                break;
            default:
                throw new IllegalArgumentException(String.format("Could not handle button type %s", buttonType));
        }
        this.allButtons.put(buttonType, button);
    }

    public void handleToolbarSeperator(JToolBar.Separator toolbarSeparator) {
        toolbarSeparator.setForeground(grey);
        toolbarSeparator.setBackground(grey);
        toolbarSeparator.setSize(new Dimension(30, 30));
        toolbarSeparator.setPreferredSize(new Dimension(30, 30));
        toolbarSeparator.setMinimumSize(new Dimension(30, 30));
        toolbarSeparator.setMaximumSize(new Dimension(30, 30));
        toolbarSeparator.setSeparatorSize(new Dimension(1, 15));
    }

    public void handleToolbar(JToolBar toolbar) {
        Color backgroundControl = new Color(60, 63, 65);
        toolbar.setBackground(backgroundControl);
    }
}
