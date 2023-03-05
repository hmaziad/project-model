package org.armadillo.core.components.handlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.armadillo.core.constants.TextConstants;
import org.armadillo.core.tree.components.DebugTreeManager;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.project.Project;

public class TreeHandler {
    private final Map<Project, ProjectConfig> projectConfigMap = new HashMap<>();

    private ProjectConfig getProjectConfig(Project project) {
        return this.projectConfigMap.computeIfAbsent(project, k -> new ProjectConfig(project));
    }

    public void setSnapEnabled(boolean isEnabled, DebuggerSession session) {
        Project project = session.getProject();
        getProjectConfig(project).setSnapEnabled(isEnabled);
    }

    public void setFlowId(DebuggerSession session, LocalDateTime timestamp) {
        Project project = session.getProject();
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern(TextConstants.NODE_DATE_FORMAT));
        getProjectConfig(project).setFlowId(String.format("Flow_%s_%s", session.getSessionName(), formattedTimestamp));
    }

    public void setClearEnabled(boolean isEnabled, Project project) {
        getProjectConfig(project).setClearEnabled(isEnabled);
    }

    public boolean isSnapEnabled(Project project) {
        return getProjectConfig(project).isSnapEnabled();
    }

    public boolean isClearEnabled(Project project) {
        return getProjectConfig(project).isClearEnabled();
    }

    public DebugTreeManager getDebugTreeManager(Project project) {
        return getProjectConfig(project).getTreeManager();
    }

    public String getNodeNameInWindow(Project project) {
        return getProjectConfig(project).getNameInWindow();
    }

    public String getFlowId(Project project) {
        return getProjectConfig(project).getFlowId();
    }

    public void setNodeNameInWindow(String nameInWindow, Project project) {
        getProjectConfig(project).setNameInWindow(nameInWindow);
    }
}
