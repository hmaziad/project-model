package org.intellij.sdk.project.model.components.buttons;

import static org.intellij.sdk.project.model.constants.TextConstants.OPEN_DIFF_WINDOW;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.components.views.DiffView;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class DiffButton extends IconWithTextAction implements ReachServices {

    public DiffButton() {
        super(null, OPEN_DIFF_WINDOW, SdkIcons.DIFF_ICON);
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
