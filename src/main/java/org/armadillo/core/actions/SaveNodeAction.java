package org.armadillo.core.actions;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.tree.components.DebugNode;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;

public class SaveNodeAction extends XDebuggerTreeActionBase implements ReachServices {
    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {
        DebugNode root = new DebugNode();
        root.add(new DebugNode(node));

        nodeHandler.save(DebugNodeContainer.builder().node(root).build(), e.getProject());
        treeHandler.getDebugTreeManager(e.getProject()).setRoot(root);
    }
}
