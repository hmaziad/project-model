package org.intellij.sdk.project.model.tree.components;

import java.awt.*;

import com.intellij.ui.ColorUtil;

import lombok.Getter;

@Getter
public enum DebugColor {
    RED(ColorUtil.fromHex("#780C0C")), GREEN(ColorUtil.fromHex("#384C38")), BLUE(ColorUtil.fromHex("#003C84"));

    private final Color realColor;

    DebugColor(Color realColor) {
        this.realColor = realColor;
    }
}
