package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.START_DEBUGGER_ERROR_MESSAGE;

import java.util.Objects;

import javax.swing.*;
import org.intellij.sdk.project.model.services.ComputeChildrenService;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public abstract class AbstractToolHandler {
    private final XDebuggerManager xDebuggerManager;
    private JLabel feedbackLabel;
    protected ComputeChildrenService computeChildrenService;
    private final Project project;

    protected AbstractToolHandler(ComputeChildrenService computeChildrenService, Project project, JLabel feedbackLabel) {
        this.computeChildrenService = computeChildrenService;
        this.project = project;
        this.xDebuggerManager = XDebuggerManager.getInstance(project);
        this.feedbackLabel = feedbackLabel;
    }

    protected boolean loadDebuggerSession() {
        XDebugSession currentSession = this.xDebuggerManager.getCurrentSession();
        if (Objects.isNull(currentSession)) {
            getFeedbackLabel().setText(START_DEBUGGER_ERROR_MESSAGE);
            return false;
        }

        LOG.info("Debug Session Retrieved...");
        this.computeChildrenService.initStackFrame(currentSession.getCurrentStackFrame());
        LOG.info("Start Computing Children...");
        return true;
    }

}
