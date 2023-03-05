package org.armadillo.core.tree.components;

import java.awt.*;

import org.jetbrains.annotations.NotNull;
import com.intellij.ui.ColorUtil;

import lombok.Getter;

@Getter
public enum DebugColor {
    RED(Constants.RED_COLOR), GREEN(Constants.GREEN_COLOR), BLUE(Constants.BLUE_COLOR);

    private final Color realColor;

    DebugColor(Color realColor) {
        this.realColor = realColor;
    }

    private static class Constants {
        private static final @NotNull Color RED_COLOR = ColorUtil.fromHex("#663a3a");
        private static final @NotNull Color GREEN_COLOR = ColorUtil.fromHex("#384C38");
        private static final @NotNull Color BLUE_COLOR = ColorUtil.fromHex("#3a4757");
    }
}
