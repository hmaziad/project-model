package org.intellij.sdk.project.model.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

// use this tool instead https://github.com/google/diff-match-patch


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ParserService {
    private static final int INDENTS_PART = 0;
    private static final int TEXT_PART = 1;
    private static final int PARTS = 4;
    private static final String SPACE = " ";

    public static DebugNode convertStringsToNode(List<String> lines) {
        try {
            List<String[]> lineArrays = new ArrayList<>();
            for (String line : lines) {
                String[] lineArray = line.split(",", PARTS);
                lineArrays.add(lineArray);
            }
            Map<Integer, DebugNode> nodePerIndex = new HashMap<>();
            nodePerIndex.put(-1, createDummyNode());
            for (String[] lineArray : lineArrays) {
                String indents = lineArray[INDENTS_PART];
                int numberOfIndents = indents.length();
                DebugNode newNode = createNode(lineArray[TEXT_PART]);
                nodePerIndex.put(numberOfIndents, newNode);
                nodePerIndex.get(numberOfIndents - 1).add(newNode);
            }
            return nodePerIndex.get(-1);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Could not parse %s to a node", lines), e);
        }
    }

    @NotNull
    private static DebugNode createDummyNode() {
        return createNode("name nodeId value");
    }

    private static DebugNode createNode(String text) {
        return DebugNode.createNode(text);
    }

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