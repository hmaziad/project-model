package org.intellij.sdk.project.model.components.handlers;

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

public class DiffHandler {

    public void diffNodes(DebugNode leftNode, String leftNodeName, DebugNode rightNode, String rightNodeName, Project project) {
        String leftNodeString = ParserService.convertNodeToString(leftNode);
        String rightNodeString = ParserService.convertNodeToString(rightNode);
        DiffContent content1 = new DocumentContentImpl(project, new DocumentImpl(leftNodeString), null);
        DiffContent content2 = new DocumentContentImpl(project, new DocumentImpl(rightNodeString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1(leftNodeName);
        chain.setTitle2(rightNodeName);
        chain.setWindowTitle("Comparing Sessions");
        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT);
    }

}

