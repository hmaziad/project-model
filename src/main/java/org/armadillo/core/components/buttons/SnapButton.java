package org.armadillo.core.components.buttons;

import static org.armadillo.core.constants.TextConstants.GET_PAID_VERSION;
import static org.armadillo.core.constants.TextConstants.REGISTER_PLUGIN;

import java.util.Optional;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.constants.MessageDialogues;
import org.armadillo.core.constants.TextConstants;
import org.armadillo.core.license.CheckLicense;
import org.armadillo.core.tree.components.DebugNode;
import org.armadillo.core.tree.components.DebugNodeContainer;
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
        super(null, TextConstants.SAVE_DEBUGGER_SESSION, SdkIcons.SNAP_ICON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (Boolean.FALSE.equals(CheckLicense.isLicensed()) && nodeHandler.getAllContainersPerNames().size() >= 8) {
            MessageDialogues.getErrorMessageDialogue(GET_PAID_VERSION, e.getProject());
            CheckLicense.requestLicense(REGISTER_PLUGIN);
            return;
        }

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
