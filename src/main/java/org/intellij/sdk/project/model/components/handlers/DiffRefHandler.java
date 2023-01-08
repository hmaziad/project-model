package org.intellij.sdk.project.model.components.handlers;

import java.util.Objects;

import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.DropdownObserver;
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
    Project project;
    DropdownObserver leftDropdownObserver;
    DropdownObserver rightDropdownObserver;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        String leftNodeName = this.leftDropdownObserver.getCurrentItem();
        DebugNode leftNode = getNodeFromDropdown(leftNodeName);
        String rightNodeName = this.rightDropdownObserver.getCurrentItem();
        DebugNode rightNode = getNodeFromDropdown(rightNodeName);

        String leftNodeString = ParserService.convertNodeToString(leftNode);
        String rightNodeString = ParserService.convertNodeToString(rightNode);
        DiffContent content1 = new DocumentContentImpl(this.project, new DocumentImpl(leftNodeString), null);
        DiffContent content2 = new DocumentContentImpl(this.project, new DocumentImpl(rightNodeString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1(leftNodeName);
        chain.setTitle2(rightNodeName);
        chain.setWindowTitle("Comparing Saved nodes");
        DiffManager.getInstance().showDiff(this.project, chain, DiffDialogHints.DEFAULT);
    }

    private DebugNode getNodeFromDropdown(String nodeName) {
        if (Objects.isNull(nodeName)) {
//            this.feedbackLabel.setText(SELECTED_LABEL_IS_NULL);
        }

        DebugNode selectedNode = persistencyService.getNodes().get(nodeName);
        if (Objects.isNull(selectedNode)) {
//            this.feedbackLabel.setText(SELECTED_NODE_IS_NULL);
        }
        return selectedNode;
    }

}

