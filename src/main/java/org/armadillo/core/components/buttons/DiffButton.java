package org.armadillo.core.components.buttons;

import org.armadillo.core.components.views.DiffView;
import org.armadillo.core.constants.TextConstants;
import org.armadillo.core.components.handlers.ReachServices;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class DiffButton extends IconWithTextAction implements ReachServices {

    public DiffButton() {
        super(null, TextConstants.OPEN_DIFF_WINDOW, SdkIcons.DIFF_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new DiffView(e.getProject()).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(!nodeHandler.getAllContainersPerNames().isEmpty());
    }
}
