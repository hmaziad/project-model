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
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class SnapButton extends IconWithTextAction implements ReachServices {

    public SnapButton() {
        super(null, SAVE_DEBUGGER_SESSION, SdkIcons.SNAP_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional<DebugNodeContainer> optionalDebugNode = snapHandler.getCurrentSession(e.getProject());
        if (optionalDebugNode.isEmpty()) {
            LOG.warn("Weird, we cannot get a debug session. Try another way.");
        } else {
            DebugNodeContainer debugNodeContainer = optionalDebugNode.get();
            DebugNode debugNode = debugNodeContainer.getNode();
            String nodeName = nodeHandler.save(debugNodeContainer, e.getProject());
            treeHandler.getDebugTreeManager(e.getProject()).setRoot(debugNode);
            treeHandler.setNodeNameInWindow(nodeName, e.getProject());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(treeHandler.isSnapEnabled(e.getProject()));
    }
}
