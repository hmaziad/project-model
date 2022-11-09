package org.intellij.sdk.project.model.components;

import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.ComputeChildrenService;
import com.intellij.openapi.project.Project;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SnapHandler extends AbstractToolHandler {

    public SnapHandler(ComputeChildrenService computeChildrenService, Project project, JLabel feedbackLabel) {
        super(computeChildrenService, project, feedbackLabel);
    }

    public void snap(DefaultTreeModel treeModel) {
        boolean isLoaded = loadDebuggerSession();
        if (isLoaded) {
            CompletableFuture.runAsync(() -> this.computeChildrenService.execute(treeModel::setRoot));
        }
    }

}
