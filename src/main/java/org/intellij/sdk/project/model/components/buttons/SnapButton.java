package org.intellij.sdk.project.model.components.buttons;

import static org.intellij.sdk.project.model.constants.TextConstants.SAVE_DEBUGGER_SESSION;

import java.util.Optional;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;

import icons.SdkIcons;
import lombok.Getter;

@Getter
public class SnapButton extends IconWithTextAction implements ReachServices {

    public SnapButton() {
        super(null, SAVE_DEBUGGER_SESSION, SdkIcons.SNAP_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<DebugNodeContainer> optionalDebugNode = COMPONENT_SERVICE.getSnapHandler().getCurrentSession(e.getProject());
        if (optionalDebugNode.isEmpty()) {
            COMPONENT_SERVICE.getFeedbackMessage().setText("Weird, we cannot get a debug session. Try another way.");
        } else {
//            MarkupModel markupModel = e.getSelectedTextEditor().getMarkupModel();
            DebugNodeContainer debugNodeContainer = optionalDebugNode.get();
            DebugNode debugNode = debugNodeContainer.getNode();
            COMPONENT_SERVICE.getNodeHandler().save(debugNodeContainer, e.getProject());
            COMPONENT_SERVICE.getDebugTreeManager().setRoot(debugNode);
//            Editor editor = (Editor) e.getDataContext().getData("host.editor");
//            MarkupModel markupModel = editor.getMarkupModel();

        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(COMPONENT_SERVICE.getSnapIsEnabled().get());
    }
}
