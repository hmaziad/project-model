package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.START_DEBUGGER_ERROR_MESSAGE;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.ComputeChildrenService;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class SnapHandler implements ToolHandler {
    private static final ComputeChildrenService computeChildrenService = new ComputeChildrenService();
    private final Project project;
    private final JLabel feedbackLabel;

    public void handle(DefaultTreeModel treeModel) {
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(this.project);
        boolean isLoaded = loadDebuggerSession(xDebuggerManager);
        if (isLoaded) {
            CompletableFuture.runAsync(() -> computeChildrenService.execute(treeModel::setRoot));
        }
    }

    protected boolean loadDebuggerSession(XDebuggerManager xDebuggerManager) {
        XDebugSession currentSession = xDebuggerManager.getCurrentSession();
        if (Objects.isNull(currentSession)) {
            this.feedbackLabel.setText(START_DEBUGGER_ERROR_MESSAGE);
            return false;
        }

        LOG.info("Debug Session Retrieved...");
        computeChildrenService.initStackFrame(currentSession.getCurrentStackFrame());
        LOG.info("Start Computing Children...");
        return true;
    }
}
