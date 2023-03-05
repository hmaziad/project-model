package org.armadillo.core.components.buttons;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.components.views.settings.SettingsView;
import org.armadillo.core.constants.TextConstants;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class SettingsButton extends IconWithTextAction implements ReachServices {

    public SettingsButton() {
        super(null, TextConstants.OPEN_SETTINGS_WINDOW, SdkIcons.VIEW_NODES_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SettingsView(e.getProject()).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
    }
}
