package org.intellij.sdk.project.model.components.handlers;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

public class ToolbarButton extends IconWithTextAction {

    public ToolbarButton(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        this.presentation.setEnabled(!this.presentation.isEnabled());
    }

}
