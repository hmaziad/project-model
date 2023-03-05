package org.armadillo.core.components.handlers;

import org.armadillo.core.tree.components.DebugTreeManager;

import com.intellij.openapi.project.Project;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ProjectConfig {
    private final DebugTreeManager treeManager = new DebugTreeManager(false);
    @Setter
    private boolean isSnapEnabled = false;
    @Setter
    private boolean isClearEnabled = false;
    @Setter
    private String nameInWindow;
    @Setter
    private String flowId;
    public ProjectConfig(Project project) {
        this.treeManager.setProject(project);
    }
}
