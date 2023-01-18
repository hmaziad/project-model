package org.intellij.sdk.project.model.components.handlers;

import java.util.Optional;

import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class SnapButton extends IconWithTextAction implements ReachServices {

    private final SnapHandler snapHandler = new SnapHandler();

    public SnapButton() {
        super(null, "Save Debugger Session", SdkIcons.SNAP_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<DebugNode> debugNode = this.snapHandler.getCurrentSession(e.getProject());
        if (debugNode.isEmpty()) {
            COMPONENT_SERVICE.getFeedbackMessage().setText("Weird, we cannot get a debug session. Try another way.");
        } else {
            ((DefaultTreeModel) COMPONENT_SERVICE.getDebugTree().getModel()).setRoot(debugNode.get());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(COMPONENT_SERVICE.getSnapIsEnabled().get());
    }
}
