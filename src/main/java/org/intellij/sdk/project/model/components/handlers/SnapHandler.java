package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.util.HelperUtil.getPackageNameFromVfs;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import org.intellij.sdk.project.model.tree.components.DebugFrame;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.content.Content;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XDebuggerTreeNode;
import com.sun.jdi.Location;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SnapHandler {

    public Optional<DebugNodeContainer> getCurrentSession(Project project) {
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(project);
        XDebugSession currentSession = xDebuggerManager.getCurrentSession();
        if (!Objects.isNull(currentSession)) {
            XSourcePosition sourcePosition = currentSession.getCurrentStackFrame().getSourcePosition();
            int lineNumber = sourcePosition.getLine();
            LOG.info("Debugger session exists");
            XDebuggerTreeNode xRootNode = getDebugSessionTree(xDebuggerManager);
            LOG.debug("Debugger session retrieved: {}", xRootNode);
            DebugNode resultNode = new DebugNode(xRootNode);
            List<DebugFrame> frames = getFrames(currentSession);

            DebugNodeContainer container = DebugNodeContainer.builder() //
                .frames(frames) //
                .node(resultNode) //
                .lineNumber(lineNumber) //
                .packageName(getPackageNameFromVfs(sourcePosition.getFile(), project).orElse(null)) //
                .build();

            return Optional.of(container);
        }

        return Optional.empty();
    }

    private List<DebugFrame> getFrames(XDebugSession currentSession) {
        Content framesContent = Arrays //
            .stream(currentSession.getUI().getContents()) //
            .filter(c -> "Frames".equals(c.getDisplayName())) //
            .findFirst() //
            .orElseThrow();
        JComponent component = (JComponent) Arrays //
            .stream(framesContent.getComponent().getComponents()) //
            .filter(JBScrollPane.class::isInstance) //
            .findFirst() //
            .orElseThrow(); //
        JComponent component1 = (JComponent) Arrays //
            .stream(component.getComponents()) //
            .filter(JBViewport.class::isInstance) //
            .findFirst() //
            .orElseThrow(); //
        XDebuggerFramesList component2 = (XDebuggerFramesList) component1.getComponents()[0]; // only one

        List<?> items = component2.getModel().getItems();
        List<DebugFrame> debugFrames = new ArrayList<>();
        for (Object item : items) {
            JavaStackFrame stackFrame = (JavaStackFrame) item;
            Location location = stackFrame.getDescriptor().getLocation();
            if (Objects.nonNull(location)) {
                String methodName = location.method().name();
                int lineNumber = location.lineNumber();
                String packageWithName = location.declaringType().name();
                debugFrames.add(new DebugFrame(methodName, lineNumber, packageWithName));
            }
        }
        return debugFrames;
    }

    private XDebuggerTreeNode getDebugSessionTree(XDebuggerManager xDebuggerManager) {
        JComponent parentComponent = (JComponent) ((JComponent) xDebuggerManager.getCurrentSession().getUI().getContents()[1].getComponent().getComponent(0)).getComponent(0);
        Component[] components = parentComponent.getComponents();
        JBViewport viewport = null;
        for (var component : components) {
            if (component instanceof JBViewport) {
                viewport = (JBViewport) component;
            }
        }
        return (XDebuggerTreeNode) ((XDebuggerTree) viewport.getComponent(0)).getTreeModel().getRoot();
    }

}