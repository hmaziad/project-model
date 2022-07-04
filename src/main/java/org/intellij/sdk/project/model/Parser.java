package org.intellij.sdk.project.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;

public class Parser {
    private static final int INDEX = 0;
    private static final int NAME = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;
    private static final int PARTS = 4;

    public static XTestCompositeNode parse(Path path) {
        try {
            return parse(Files.readAllLines(path), new ArrayList<>());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static XTestCompositeNode parse(List<String> lines, List<XTestCompositeNode> diffNodes) {
        List<String[]> lineArrays = new ArrayList<>();
        for (String line : lines) {
            String[] lineArray = line.split(",", PARTS);
            lineArrays.add(lineArray);
        }

        Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
        for (String[] lineArray : lineArrays) {
            String first = lineArray[INDEX];
            char ch = ' ';
            int index = first.length();
            if (!first.isEmpty() && (first.charAt(0) == '+' || first.charAt(0) == '-')) {
                ch = first.charAt(0);
                index--;
            }
            XTestCompositeNode newNode = createNode(lineArray, ch);
            if (newNode.getDiffChar() == '+' || newNode.getDiffChar() == '-') {
                diffNodes.add(newNode);
            }
            nodePerIndex.put(index, newNode);
            if (index == 0) {
                continue;
            }
            if (nodePerIndex.get(index - 1) == null) {
                System.out.println("Stop");
            }

            nodePerIndex.get(index - 1).addChild(newNode);
        }
        return nodePerIndex.get(INDEX);
    }

    private static XTestCompositeNode createNode(String[] next, char ch) {
        return XTestCompositeNode.createNode(next[NAME], next[TYPE], next[VALUE], ch);
    }

    public static String deParse(XTestCompositeNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node, "");
        return sb.toString();
    }

    private static void addLines(StringBuilder sb, XTestCompositeNode node, String spaces) {
        sb.append(spaces)
          .append(",")
          .append(node.getContainer().toString())
          .append(",")
          .append(node.getNodeId())
          .append(",")
          .append(node.getValue())
          .append("\n");
        node.getChildren().forEach(child -> addLines(sb, child, spaces + " "));
    }

    public static String deParse(List<String> diffLines) {
        return String.join("\n", diffLines);
    }

}
