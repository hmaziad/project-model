package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.START_DEBUGGER_ERROR_MESSAGE;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBViewport;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XStackFrameNode;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class SnapHandler implements ToolHandler {
    private final Project project;
    private final JLabel feedbackLabel;
    private final Consumer<DebugNode> consumer;

    public void handle(DefaultTreeModel treeModel) {
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(this.project);
        boolean isLoaded = loadDebuggerSession(xDebuggerManager);
        if (isLoaded) {
            XStackFrameNode xRootNode = getTreeModel(xDebuggerManager);
            DebugNode rootNode = new DebugNode(xRootNode);

            this.consumer.accept(rootNode);
        }
    }

    private XStackFrameNode getTreeModel(XDebuggerManager xDebuggerManager) {
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

    protected boolean loadDebuggerSession(XDebuggerManager xDebuggerManager) {
        XDebugSession currentSession = xDebuggerManager.getCurrentSession();
        if (Objects.isNull(currentSession)) {
            this.feedbackLabel.setText(START_DEBUGGER_ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
