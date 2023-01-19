package org.intellij.sdk.project.model.components;

import javax.swing.*;
import org.intellij.sdk.project.model.components.buttons.ClearButton;
import org.intellij.sdk.project.model.components.buttons.DiffButton;
import org.intellij.sdk.project.model.components.buttons.SettingsButton;
import org.intellij.sdk.project.model.components.buttons.SnapButton;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

public class DebuggerToolbar extends SimpleToolWindowPanel {

    public DebuggerToolbar(boolean vertical) {
        super(vertical, false);

        final ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP", false);

        IconWithTextAction snapButton = new SnapButton();
        IconWithTextAction clearButton = new ClearButton();
        IconWithTextAction diffButton = new DiffButton();
        IconWithTextAction settingsButton = new SettingsButton();

        actionGroup.add(snapButton);
        actionGroup.addSeparator();
        actionGroup.add(diffButton);
        actionGroup.addSeparator();
        actionGroup.add(settingsButton);
        actionGroup.addSeparator();
        actionGroup.add(clearButton);
        ActionToolbar actionToolbar = actionManager.createActionToolbar("ACTION_TOOLBAR", actionGroup, true);
        actionToolbar.setOrientation(SwingConstants.HORIZONTAL);
        this.setToolbar(actionToolbar.getComponent());
    }

}
