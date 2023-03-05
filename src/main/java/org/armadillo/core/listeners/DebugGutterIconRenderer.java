package org.armadillo.core.listeners;

import javax.swing.*;
import org.armadillo.core.components.views.settings.SettingsView;
import org.armadillo.core.constants.TextConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;

import icons.SdkIcons;

public class DebugGutterIconRenderer extends GutterIconRenderer {
    private final Integer lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public DebugGutterIconRenderer(Integer lineNumber) {
        super();
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public @NotNull Icon getIcon() {
        return SdkIcons.ARMADILLO_13;
    }

    @Override
    public @Nullable String getTooltipText() {
        return TextConstants.SHOW_SNAPS_HERE;
    }

    @Override
    public @Nullable AnAction getClickAction() {
        return new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                new SettingsView(e.getProject(), lineNumber).showAndGet();
            }
        };
    }
}
