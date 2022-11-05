package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ParserService {
    private static final int INDENTS_PART = 0;
    private static final int NAME_PART = 1;
    private static final int TYPE_PART = 2;
    private static final int VALUE_PART = 3;
    private static final int PARTS = 4;
    private static final char SPACE = ' ';
// use this tool instead https://github.com/google/diff-match-patch
    public static XTestCompositeNode convertStringsToNode(List<String> lines) {
        try {
            List<String[]> lineArrays = new ArrayList<>();
            for (String line : lines) {
                String[] lineArray = line.split(",", PARTS);
                lineArrays.add(lineArray);
            }
            Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
            nodePerIndex.put(-1, createDummyNode());
            for (int lineNumber = 0; lineNumber < lineArrays.size(); lineNumber++) {
                String[] lineArray = lineArrays.get(lineNumber);
                int numberOfIndents = Integer.parseInt(lineArray[INDENTS_PART]);
                XTestCompositeNode newNode = createNode(lineArray, SPACE, lineNumber);
                nodePerIndex.put(numberOfIndents, newNode);
                nodePerIndex.get(numberOfIndents - 1).addChild(newNode);
            }
            return nodePerIndex.get(-1);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Could not parse %s to a node", lines), e);
        }
    }

    @NotNull
    private static XTestCompositeNode createDummyNode() {
        return createNode(new String[] {"", "name", "nodeId", "value"}, SPACE, -1);
    }

    private static XTestCompositeNode createNode(String[] next, char signOrSpace, int lineNumber) {
        return XTestCompositeNode.createNode(next[NAME_PART], next[TYPE_PART], next[VALUE_PART], signOrSpace, lineNumber);
    }

    // TODO below methods have to be removed

    public static List<String> convertNodeToStrings(XTestCompositeNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node.getChildren(), "");
        return Arrays.stream(sb.toString().split("\n")).collect(Collectors.toList());
    }

    private static void addLines(StringBuilder sb, List<XTestCompositeNode> nodes, String spaces) {
        nodes.forEach(child -> {
            appendData(sb, child, spaces);
            addLines(sb, child.getChildren(), spaces + " ");
        });
    }


    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void appendData(StringBuilder sb, XTestCompositeNode node, String spaces) {
        sb.append(spaces) //
            .append(",") //
            .append(node.getContainer().toString()) //
            .append(",") //
            .append(node.getNodeId()) //
            .append(",") //
            .append(node.getValue()) //
            .append("\n");
    }


    private static void print(XTestCompositeNode node, String tab) {
        LOG.info(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }
}