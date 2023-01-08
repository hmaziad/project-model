package org.intellij.sdk.project.model.components.handlers;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.ParserService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiffHandler implements ToolHandler {
    private final JLabel feedbackLabel;
    private final Project project;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        DebugNode originalNode = (DebugNode) treeModel.getRoot();
        ToolHandler snapHandler = new SnapHandler(this.project, this.feedbackLabel, revisedNode -> computeDiff(originalNode, revisedNode));
        snapHandler.handle(treeModel);
    }

    private void computeDiff(DebugNode originalNode, DebugNode revisedNode) {
        String savedSnapString = ParserService.convertNodeToString(originalNode);
        String debuggingSessionString = ParserService.convertNodeToString(revisedNode);
        DiffContent content1 = new DocumentContentImpl(this.project, new DocumentImpl(debuggingSessionString), null);
        DiffContent content2 = new DocumentContentImpl(this.project, new DocumentImpl(savedSnapString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1("Debugger Session");
        chain.setTitle2("Snapshot Session");
        chain.setWindowTitle("Comparing Debugger and Snapshot session");
        DiffManager.getInstance().showDiff(this.project, chain, DiffDialogHints.DEFAULT);
        this.feedbackLabel.setText("Successfully computed diff");
    }
}

