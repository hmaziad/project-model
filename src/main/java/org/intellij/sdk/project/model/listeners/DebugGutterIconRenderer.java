package org.intellij.sdk.project.model.listeners;

import static org.intellij.sdk.project.model.constants.TextConstants.SHOW_SNAPS_HERE;

import javax.swing.*;
import org.intellij.sdk.project.model.components.views.settings.SettingsView;
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
        return SdkIcons.VIEW_NODES_ICON_SMALL;
    }

    @Override
    public @Nullable String getTooltipText() {
        return SHOW_SNAPS_HERE;
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
