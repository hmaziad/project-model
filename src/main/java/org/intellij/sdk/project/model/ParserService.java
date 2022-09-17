package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ParserService {
    private static final int INDENTS_SIGN_PART = 0;
    private static final int NAME_PART = 1;
    private static final int TYPE_PART = 2;
    private static final int VALUE_PART = 3;
    private static final int PARTS = 4;
    private static final char SPACE = ' ';
// use this tool instead https://github.com/google/diff-match-patch
    public static XTestCompositeNode parseStringsToNode(List<String> lines, List<XTestCompositeNode> diffNodes) {
        List<String[]> lineArrays = new ArrayList<>();
        diffNodes.clear();
        for (String line : lines) {
            String[] lineArray = line.split(",", PARTS);
            lineArrays.add(lineArray);
        }

        Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
        nodePerIndex.put(-1, createDummyNode());
        for (int lineNumber = 0; lineNumber < lineArrays.size(); lineNumber++) {
            String[] lineArray = lineArrays.get(lineNumber);
            String indentsAndSign = lineArray[INDENTS_SIGN_PART];
            char signOrSpace = SPACE;
            int indents = indentsAndSign.length();
            if (!indentsAndSign.isEmpty() && (indentsAndSign.charAt(0) == '+' || indentsAndSign.charAt(0) == '-')) {
                signOrSpace = indentsAndSign.charAt(0);
                indents--;
            }
            XTestCompositeNode newNode = createNode(lineArray, signOrSpace, lineNumber);
            if (newNode.getDiffChar() == '+' || newNode.getDiffChar() == '-') {
                diffNodes.add(newNode);
            }
            nodePerIndex.put(indents, newNode);
            nodePerIndex.get(indents - 1).addChild(newNode);
        }
        return nodePerIndex.get(-1);
    }

    @NotNull
    private static XTestCompositeNode createDummyNode() {
        return createNode(new String[] {"", "name", "nodeId", "value"}, SPACE, -1);
    }

    private static XTestCompositeNode createNode(String[] next, char signOrSpace, int lineNumber) {
        return XTestCompositeNode.createNode(next[NAME_PART], next[TYPE_PART], next[VALUE_PART], signOrSpace, lineNumber);
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

    public static List<String> writeNodeAsString(XTestCompositeNode node) {
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

    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
        LOG.info(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }
}
