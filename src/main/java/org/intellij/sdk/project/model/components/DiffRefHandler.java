package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.SELECTED_LABEL_IS_NULL;
import static org.intellij.sdk.project.model.constants.TextConstants.SELECTED_NODE_IS_NULL;

import java.util.Objects;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.ParserService;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;

import lombok.Value;

@Value
public class DiffRefHandler implements ToolHandler {
    static PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    JLabel feedbackLabel;
    Project project;
    DropdownObserver savedDropdownObserver;
    DropdownObserver refDropdownObserver;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        DebugNode savedNode = getNodeFromDropdown(this.savedDropdownObserver);
        DebugNode refSavedNode = getNodeFromDropdown(this.refDropdownObserver);

        String savedSnapString = ParserService.convertNodeToStrings(savedNode);
        String debuggingSessionString = ParserService.convertNodeToStrings(refSavedNode);
        DiffContent content1 = new DocumentContentImpl(this.project, new DocumentImpl(debuggingSessionString), null);
        DiffContent content2 = new DocumentContentImpl(this.project, new DocumentImpl(savedSnapString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1("Saved Node");
        chain.setTitle2("Ref Saved Node");
        chain.setWindowTitle("Comparing Saved nodes");
        DiffManager.getInstance().showDiff(this.project, chain, DiffDialogHints.DEFAULT);
        this.feedbackLabel.setText("Successfully computed diff");
    }

    private DebugNode getNodeFromDropdown(DropdownObserver dropdownObserver) {
        String selectedItemLabel = dropdownObserver.getCurrentItem();
        if (Objects.isNull(selectedItemLabel)) {
            this.feedbackLabel.setText(SELECTED_LABEL_IS_NULL);
        }

        DebugNode selectedNode = persistencyService.getNodes().get(selectedItemLabel);
        if (Objects.isNull(selectedNode)) {
            this.feedbackLabel.setText(SELECTED_NODE_IS_NULL);
        }
        return selectedNode;
    }

    private void computeDiff(DebugNode originalNode, DebugNode revisedNode) {
        String savedSnapString = ParserService.convertNodeToStrings(originalNode);
        String debuggingSessionString = ParserService.convertNodeToStrings(revisedNode);
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

