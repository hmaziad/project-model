package org.armadillo.core.components.buttons;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.components.views.flow.FlowView;
import org.armadillo.core.constants.TextConstants;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class FlowButton extends IconWithTextAction implements ReachServices {
    public FlowButton() {
        super(null, TextConstants.FLOWS_TITLE, SdkIcons.FLOW_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new FlowView(e.getProject()).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(!nodeHandler.getAllContainersPerNames().isEmpty());
    }
}
