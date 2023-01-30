package org.intellij.sdk.project.model.components.buttons;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SESSION_BELOW;

import java.util.List;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class DeleteButton extends IconWithTextAction implements ReachServices {
    public DeleteButton() {
        super(null, DELETE_SESSION_BELOW, SdkIcons.DELETE_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String nodeNameInWindow = COMPONENT_SERVICE.getNodeNameInWindow();
        COMPONENT_SERVICE.getNodeHandler().delete(List.of(nodeNameInWindow),e.getProject());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(COMPONENT_SERVICE.getClearIsEnabled().get());
    }
}
