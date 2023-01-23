package org.intellij.sdk.project.model.components.toolbar;

import javax.swing.*;
import org.intellij.sdk.project.model.components.buttons.ClearButton;
import org.intellij.sdk.project.model.components.buttons.DeleteButton;
import org.intellij.sdk.project.model.components.buttons.DiffButton;
import org.intellij.sdk.project.model.components.buttons.SettingsButton;
import org.intellij.sdk.project.model.components.buttons.SnapButton;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.util.ui.JBUI;

public class DebuggerWindowContent extends SimpleToolWindowPanel {
// JBUI.CurrentTheme.Link.Foreground.ENABLED
    public DebuggerWindowContent(boolean vertical) {
        super(vertical, false);

        final ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP", false);

        IconWithTextAction snapButton = new SnapButton();
        IconWithTextAction deleteButton = new DeleteButton();
        IconWithTextAction clearButton = new ClearButton();
        IconWithTextAction diffButton = new DiffButton();
        IconWithTextAction settingsButton = new SettingsButton();

        actionGroup.add(snapButton);
        actionGroup.add(clearButton);
        actionGroup.addSeparator();
        actionGroup.add(diffButton);
        actionGroup.addSeparator();
        actionGroup.add(settingsButton);
        actionGroup.addSeparator();
        actionGroup.add(deleteButton);

        ActionToolbar actionToolbar = actionManager.createActionToolbar("ACTION_TOOLBAR", actionGroup, true);
        actionToolbar.setOrientation(SwingConstants.HORIZONTAL);
        actionToolbar.getComponent().setBorder(JBUI.Borders.customLineBottom(JBUI.CurrentTheme.ToolWindow.borderColor()));
        this.setToolbar(actionToolbar.getComponent());
    }

}
