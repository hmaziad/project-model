package org.intellij.sdk.project.model.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.DiffService;
import org.intellij.sdk.project.model.services.ParserService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.project.Project;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiffHandler implements ToolHandler {
    private final JLabel feedbackLabel;
    private final Project project;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        DebugNode originalNode = (DebugNode) treeModel.getRoot();
        ToolHandler snapHandler = new SnapHandler(this.project, this.feedbackLabel, revisedNode -> computeDiff(originalNode, revisedNode, treeModel));
        snapHandler.handle(treeModel);
    }

    private void computeDiff(DebugNode originalNode, DebugNode revisedNode, DefaultTreeModel treeModel) {
        var originalStrings = ParserService.convertNodeToStrings(originalNode);
        var revisedStrings = ParserService.convertNodeToStrings(revisedNode);
        var diffStrings = DiffService.diffStrings(originalStrings, revisedStrings);
        var diffNode = ParserService.convertDiffStringsToNode(diffStrings);
        treeModel.setRoot(diffNode);
        this.feedbackLabel.setText("Successfully computed diff");
    }
}
