package org.armadillo.core.components.buttons;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.constants.TextConstants;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class ClearButton extends IconWithTextAction implements ReachServices {
    public ClearButton() {
        super(null, TextConstants.CLEAR_SESSION_BELOW, SdkIcons.CLEAR_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        treeHandler.getDebugTreeManager(e.getProject()).setRoot(null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(treeHandler.isClearEnabled(e.getProject()));
    }
}
