package org.armadillo.core.actions;

import static org.armadillo.core.constants.TextConstants.GET_PAID_VERSION;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.constants.MessageDialogues;
import org.armadillo.core.license.CheckLicense;
import org.armadillo.core.tree.components.DebugNode;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;

public class SaveNodeAction extends XDebuggerTreeActionBase implements ReachServices {

    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {
        if (Boolean.FALSE.equals(CheckLicense.isLicensed()) && nodeHandler.getAllContainersPerNames().size() >= 8) {
            MessageDialogues.getErrorMessageDialogue(GET_PAID_VERSION, e.getProject());
            return;
        }

        DebugNode root = new DebugNode();
        root.add(new DebugNode(node));

        nodeHandler.save(DebugNodeContainer.builder().node(root).build(), e.getProject()); //  todo use returned value
        treeHandler.getDebugTreeManager(e.getProject()).setRoot(root);
    }
}
