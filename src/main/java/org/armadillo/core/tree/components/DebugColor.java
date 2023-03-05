package org.armadillo.core.tree.components;

import java.awt.*;

import com.intellij.ui.ColorUtil;

import lombok.Getter;

@Getter
public enum DebugColor {
    RED(ColorUtil.fromHex("#663a3a")), GREEN(ColorUtil.fromHex("#384C38")), BLUE(ColorUtil.fromHex("#3a4757"));

    private final Color realColor;

    DebugColor(Color realColor) {
        this.realColor = realColor;
    }
}
