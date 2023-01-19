package org.intellij.sdk.project.model.components.buttons;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.components.views.SettingsView;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class SettingsButton extends IconWithTextAction implements ReachServices {

    public SettingsButton() {
        super(null, "Open Settings Window", SdkIcons.VIEW_NODES_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SettingsView(e.getProject()).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
    }
}
