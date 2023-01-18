package org.intellij.sdk.project.model.components.handlers;

import javax.swing.tree.DefaultTreeModel;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;

public class ClearButton extends IconWithTextAction implements ReachServices {

    public ClearButton() {
        super(null, "Clear Session Below", SdkIcons.CLEAR_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ((DefaultTreeModel) COMPONENT_SERVICE.getDebugTree().getModel()).setRoot(null);
        COMPONENT_SERVICE.getFeedbackMessage().setText("Successfully cleared session");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(COMPONENT_SERVICE.getClearIsEnabled().get());
    }
}
