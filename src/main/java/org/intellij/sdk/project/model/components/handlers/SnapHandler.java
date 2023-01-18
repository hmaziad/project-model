package org.intellij.sdk.project.model.components.handlers;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBViewport;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XStackFrameNode;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SnapHandler {

    public Optional<DebugNode> getCurrentSession(Project project) {
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(project);
        if (!Objects.isNull(xDebuggerManager.getCurrentSession())) {
            LOG.info("Debugger session exists");
            XStackFrameNode xRootNode = getDebugSessionTree(xDebuggerManager);
            LOG.debug("Debugger session retrieved: {}", xRootNode);
            return Optional.of(new DebugNode(xRootNode));
        }
        return Optional.empty();
    }

    private XStackFrameNode getDebugSessionTree(XDebuggerManager xDebuggerManager) {
        JComponent parentComponent = (JComponent) ((JComponent) xDebuggerManager.getCurrentSession().getUI().getContents()[1].getComponent().getComponent(0)).getComponent(0);
        Component[] components = parentComponent.getComponents();
        JBViewport viewport = null;
        for (var component : components) {
            if (component instanceof JBViewport) {
                viewport = (JBViewport) component;
            }
        }
        return (XStackFrameNode) ((XDebuggerTree) viewport.getComponent(0)).getTreeModel().getRoot();
    }

}
