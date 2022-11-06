package org.intellij.sdk.project.model.components;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BorderHandler {

    private static final Color INTELLIJ_BLACK = new Color(45, 47, 48);
    private static final MatteBorder BLACK_BORDER = new MatteBorder(0, 0, 1, 0, INTELLIJ_BLACK);

    public static void addBottomBorder(JPanel panel) {
        panel.setBorder(BLACK_BORDER);
    }
}
