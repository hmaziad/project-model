package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.components.DropdownObserver.CURRENT_DEBUGGER_SESSION;

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
    DebugNode currentSession;

    @Override
    public void handle(DefaultTreeModel treeModel) { // remove the leftDownObservers and only send the needed nodes
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
        if (CURRENT_DEBUGGER_SESSION.equals(nodeName)) {
            return this.currentSession;
        }
        return persistencyService.getNodes().get(nodeName);
    }

}

