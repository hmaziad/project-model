package org.intellij.sdk.project.model.services;

import java.util.List;

import org.intellij.sdk.project.model.xnodes.DebugNode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

// use this tool instead https://github.com/google/diff-match-patch


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ParserService {
    private static final String SPACE = " ";

    public static String convertNodeToStrings(DebugNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node.getMyChildren(), 0);
        return sb.toString().trim();
    }

    private static void addLines(StringBuilder sb, List<DebugNode> nodes, int indents) {
        nodes.forEach(child -> {
            appendData(sb, child, indents > 0 ? SPACE.repeat(indents * 2) : "");
            addLines(sb, child.getMyChildren(), indents + 1);
        });
    }

    private static void appendData(StringBuilder sb, DebugNode node, String indents) {
        sb.append(indents) //
            .append(node.getText()) //
            .append("\n");
    }
}