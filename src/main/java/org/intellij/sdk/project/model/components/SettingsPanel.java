package org.intellij.sdk.project.model.components;

import javax.swing.*;
import org.intellij.sdk.project.model.components.handlers.ClearButton;
import org.intellij.sdk.project.model.components.handlers.DiffButton;
import org.intellij.sdk.project.model.components.handlers.SnapButton;
import org.intellij.sdk.project.model.components.handlers.ToolbarButton;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import icons.SdkIcons;

public class SettingsPanel extends SimpleToolWindowPanel {

    public SettingsPanel(boolean vertical) {
        super(vertical);

        final ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP", false);

        IconWithTextAction snapButton = new SnapButton();
        IconWithTextAction clearButton = new ClearButton();
        IconWithTextAction diffButton = new DiffButton();
        IconWithTextAction settingsButton = new ToolbarButton(null, "Open Settings Window", SdkIcons.VIEW_NODES_ICON);

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
