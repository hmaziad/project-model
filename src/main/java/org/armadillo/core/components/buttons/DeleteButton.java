package org.armadillo.core.components.buttons;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.constants.TextConstants;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class DeleteButton extends IconWithTextAction implements ReachServices {
    public DeleteButton() {
        super(null, TextConstants.DELETE_SESSION_BELOW, SdkIcons.DELETE_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String nodeNameInWindow = treeHandler.getNodeNameInWindow(e.getProject());
        nodeHandler.quickDelete(nodeNameInWindow, e.getProject());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(treeHandler.isClearEnabled(e.getProject()));
    }
}
