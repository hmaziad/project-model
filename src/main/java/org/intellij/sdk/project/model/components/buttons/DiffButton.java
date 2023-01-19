package org.intellij.sdk.project.model.components.buttons;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.components.views.DiffNodesView;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class DiffButton extends IconWithTextAction implements ReachServices {

    public DiffButton() {
        super(null, "Open Diff Window", SdkIcons.DIFF_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new DiffNodesView(e.getProject()).showAndGet();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
    }
}
