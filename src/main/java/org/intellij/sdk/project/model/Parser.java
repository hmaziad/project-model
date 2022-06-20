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
    private static final int ZERO = 0;
    private static final int INDEX = 0;
    private static final int NAME = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;
    private static final int PARTS = 4;

    public static XTestCompositeNode parse(Path path) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        List<String[]> lineArrays = new ArrayList<>();
        for (String line : lines) {
            String[] lineArray = line.split(",", PARTS);
            lineArray[ZERO] = String.valueOf(lineArray[ZERO].length());
            lineArrays.add(lineArray);
        }

        Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
        for (String[] lineArray : lineArrays) {
            int index = Integer.parseInt(lineArray[INDEX]);
            XTestCompositeNode newNode = createNode(lineArray);
            nodePerIndex.put(index, newNode);
            if (index == 0) {
                continue;
            }
            nodePerIndex.get(index - 1).addChild(newNode);
        }
        return nodePerIndex.get(ZERO);
    }

    private static XTestCompositeNode createNode(String[] next) {
        return XTestCompositeNode.createNode(next[NAME], next[TYPE], next[VALUE]);
    }

    public static String deParse(XTestCompositeNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node, "");
        return sb.toString();
    }


    private static void addLines(StringBuilder sb, XTestCompositeNode node, String tab) {
        sb.append(tab + "," + node.getContainer().toString() + "," + node.getNodeId() + "," + node.getValue() + "\n");
        node.getChildren().forEach(child -> addLines(sb, child, tab + "\t"));
    }
}
