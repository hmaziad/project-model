package org.intellij.sdk.project.model.actions;

import org.intellij.sdk.project.model.components.handlers.NodeHandler;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;

public class SaveNodeAction extends XDebuggerTreeActionBase implements ReachServices {
    private final NodeHandler nodeHandler = new NodeHandler();
    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {
        DebugNode root = new DebugNode();
        root.add(new DebugNode(node));
        this.nodeHandler.save(root);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(root);
    }
}
