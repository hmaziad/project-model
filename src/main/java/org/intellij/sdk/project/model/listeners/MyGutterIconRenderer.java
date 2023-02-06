package org.intellij.sdk.project.model.listeners;

import javax.swing.*;
import org.intellij.sdk.project.model.components.views.SettingsView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.markup.GutterIconRenderer;

import icons.SdkIcons;

public class MyGutterIconRenderer extends GutterIconRenderer {
    private final Integer lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public MyGutterIconRenderer(Integer lineNumber) {
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
        return "Show snaps at this line";
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
