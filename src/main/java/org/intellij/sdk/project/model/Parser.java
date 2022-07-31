package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Parser {
    private static final int INDEX = 0;
    private static final int NAME = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;
    private static final int PARTS = 4;

    public static XTestCompositeNode parseStringsToNode(List<String> lines, List<XTestCompositeNode> diffNodes) {
        List<String[]> lineArrays = new ArrayList<>();
        diffNodes.clear();
        for (String line : lines) {
            String[] lineArray = line.split(",", PARTS);
            lineArrays.add(lineArray);
        }

        Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
        nodePerIndex.put(-1, createNode(new String[] {"", "name", "nodeId", "value"}, ' '));
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
            nodePerIndex.get(index - 1).addChild(newNode);
        }
        return nodePerIndex.get(-1);
    }

    private static XTestCompositeNode createNode(String[] next, char ch) {
        return XTestCompositeNode.createNode(next[NAME], next[TYPE], next[VALUE], ch);
    }

    public static String writeNodeAsString(XTestCompositeNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node.getChildren(), "");
        return sb.toString();
    }

    private static void addLines(StringBuilder sb, List<XTestCompositeNode> nodes, String spaces) {
        nodes.forEach(child -> {
            appendData(sb, child, spaces);
            addLines(sb, child.getChildren(), spaces + " ");
        });
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

    public static String writeNodeAsString(List<String> diffLines) {
        return String.join("\n", diffLines);
    }

    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }

    public static List<String> unifiedDiffOfStrings(List<String> original, List<String> revised) {
        DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true).inlineDiffByWord(true).oldTag(f -> "").newTag(f -> "").build();
        List<DiffRow> rows = generator.generateDiffRows(original, revised);
        List<String> output = new ArrayList<>();
        for (DiffRow row : rows) {
            String oldLine = row.getOldLine();
            String newLine = row.getNewLine();
            if (!oldLine.equals(newLine)) {
                if (!oldLine.isEmpty()) {
                    output.add("-" + oldLine);
                }
                if (!newLine.isEmpty()) {
                    output.add("+" + newLine);
                }
            } else {
                output.add(newLine);
            }
        }
        return output;
    }
}
