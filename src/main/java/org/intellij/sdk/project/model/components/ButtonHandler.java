package org.intellij.sdk.project.model.components;

import javax.swing.*;

import icons.SdkIcons;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonHandler {
    public static void handleButton(JButton button, ButtonType buttonType) {
        button.getModel() //
            .addChangeListener(e -> button.setContentAreaFilled(button.getModel().isRollover()));

        switch (buttonType) {
            case SNAP:
                button.setIcon(SdkIcons.SnapIcon);
                break;
            case SAVE:
                button.setIcon(SdkIcons.SaveIcon);
                break;
            case DIFF:
                button.setIcon(SdkIcons.DiffIcon);
                break;
            default:
                throw new IllegalArgumentException(String.format("Could not handle button type %s", buttonType));
        }
    }
}
