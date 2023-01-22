package org.intellij.sdk.project.model.components.buttons;

import static org.intellij.sdk.project.model.constants.TextConstants.CLEAR_SESSION_BELOW;
import static org.intellij.sdk.project.model.constants.TextConstants.SUCCESSFULLY_CLEARED_SESSION;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class ClearButton extends IconWithTextAction implements ReachServices {

    public ClearButton() {
        super(null, CLEAR_SESSION_BELOW, SdkIcons.CLEAR_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
        COMPONENT_SERVICE.getFeedbackMessage().setText(SUCCESSFULLY_CLEARED_SESSION);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(COMPONENT_SERVICE.getClearIsEnabled().get());
    }
}
