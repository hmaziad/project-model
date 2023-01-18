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

public class DiffHandler implements ReachServices {

    private void computeDiff(String title1, DebugNode originalNode, String title2, DebugNode revisedNode, Project project) {
        String session1 = ParserService.convertNodeToString(originalNode);
        String session2 = ParserService.convertNodeToString(revisedNode);
        DiffContent content1 = new DocumentContentImpl(project, new DocumentImpl(session1), null);
        DiffContent content2 = new DocumentContentImpl(project, new DocumentImpl(session2), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1(title1);
        chain.setTitle2(title2);
        chain.setWindowTitle("Debugger Session");
        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT);
    }
}

