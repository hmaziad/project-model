package org.intellij.sdk.project.model.components;

import java.awt.*;

import javax.swing.*;

import icons.SdkIcons;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonHandler {
    public static final Color grey = new Color(118, 124, 130);

    public static void handleButton(JButton button, ButtonType buttonType) {
        button.getModel() //
            .addChangeListener(e -> button.setContentAreaFilled(button.getModel().isRollover()));

        switch (buttonType) {
            case SNAP:
                button.setIcon(SdkIcons.SNAP_ICON);
                break;
            case SAVE:
                button.setIcon(SdkIcons.SAVE_ICON);
                break;
            case DIFF:
                button.setIcon(SdkIcons.DIFF_ICON);
                break;
            case CLEAR:
                button.setIcon(SdkIcons.CLEAR_ICON);
                break;
            case DELETE:
                button.setIcon(SdkIcons.DELETE_ICON);
                break;
            case EXPAND:
                button.setIcon(SdkIcons.EXPAND_ICON);
                break;
            case COLLAPSE:
                button.setIcon(SdkIcons.COLLAPSE_ICON);
                break;
            case PREVIOUS:
                button.setIcon(SdkIcons.PREVIOUS_ICON);
                break;
            case NEXT:
                button.setIcon(SdkIcons.NEXT_ICON);
                break;
            default:
                throw new IllegalArgumentException(String.format("Could not handle button type %s", buttonType));
        }
    }

    public static void handleToolbarSeperator(JToolBar.Separator toolbarSeparator) {
        toolbarSeparator.setForeground(grey);
        toolbarSeparator.setBackground(grey);
        toolbarSeparator.setSize(new Dimension(30, 30));
        toolbarSeparator.setPreferredSize(new Dimension(30, 30));
        toolbarSeparator.setMinimumSize(new Dimension(30, 30));
        toolbarSeparator.setMaximumSize(new Dimension(30, 30));
        toolbarSeparator.setSeparatorSize(new Dimension(1, 15));
    }

    public static void handleToolbar(JToolBar toolbar) {
        Color backgroundControl = new Color(60, 63, 65);
        toolbar.setBackground(backgroundControl);
    }
}
