package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.COMPARING_SESSIONS;

import org.intellij.sdk.project.model.services.ParserService;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;

public class DiffHandler {

    public void diffNodes(DebugNodeContainer leftNodeContainer, String leftNodeName, DebugNodeContainer rightNodeContainer, String rightNodeName, Project project) {
        String leftNodeString = ParserService.convertNodeToString(leftNodeContainer.getNode());
        String rightNodeString = ParserService.convertNodeToString(rightNodeContainer.getNode());
        DiffContent content1 = new DocumentContentImpl(project, new DocumentImpl(leftNodeString), null);
        DiffContent content2 = new DocumentContentImpl(project, new DocumentImpl(rightNodeString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1(leftNodeName);
        chain.setTitle2(rightNodeName);
        chain.setWindowTitle(COMPARING_SESSIONS);
        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT);
    }

}

