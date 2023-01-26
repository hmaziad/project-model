package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.COMPARING_SESSIONS;

import java.util.ArrayList;
import java.util.List;

import org.intellij.sdk.project.model.services.ParserService;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.comparison.ComparisonManagerImpl;
import com.intellij.diff.comparison.ComparisonPolicy;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.fragments.LineFragment;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.project.Project;

public class DiffHandler {

    public List<List<List<Integer>>> diffNodes(DebugNode leftNode, String leftNodeName, DebugNode rightNode, String rightNodeName, Project project) {
        String leftNodeString = ParserService.convertNodeToString(leftNode);
        String rightNodeString = ParserService.convertNodeToString(rightNode);
        DiffContent content1 = new DocumentContentImpl(project, new DocumentImpl(leftNodeString), null);
        DiffContent content2 = new DocumentContentImpl(project, new DocumentImpl(rightNodeString), null);
        @NotNull MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, content2);
        chain.setTitle1(leftNodeName);
        chain.setTitle2(rightNodeName);
        chain.setWindowTitle(COMPARING_SESSIONS);
        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT);

        List<LineFragment> changes = ComparisonManagerImpl.getInstanceImpl().compareLines(leftNodeString, rightNodeString, ComparisonPolicy.DEFAULT, new ProgressIndicatorBase());

        List<List<Integer>> additions = new ArrayList<>();
        List<List<Integer>> deletions = new ArrayList<>();
        List<List<Integer>> modifications1 = new ArrayList<>();
        List<List<Integer>> modifications2 = new ArrayList<>();

        for (LineFragment change : changes) {
            if (change.getStartLine1() == change.getEndLine1()) {
                additions.add(List.of(change.getStartLine2(), change.getEndLine2()));
            } else if (change.getStartLine2() == change.getEndLine2()) {
                deletions.add(List.of(change.getStartLine1(), change.getEndLine1()));
            } else {
                modifications1.add(List.of(change.getStartLine1(), change.getEndLine1()));
                modifications2.add(List.of(change.getStartLine2(), change.getEndLine2()));
            }
        }

        return List.of(additions, deletions, modifications1, modifications2);

    }
}

