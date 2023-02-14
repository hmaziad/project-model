package org.intellij.sdk.project.model.components.handlers;

import org.intellij.sdk.project.model.tree.components.DebugTreeManager;

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

    public ProjectConfig(Project project) {
        this.treeManager.setProject(project);
    }
}
