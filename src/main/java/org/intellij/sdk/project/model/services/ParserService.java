package org.intellij.sdk.project.model.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

// use this tool instead https://github.com/google/diff-match-patch


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class ParserService {
    private static final int INDENTS_PART = 0;
    private static final int NAME_PART = 1;
    private static final int TYPE_PART = 2;
    private static final int VALUE_PART = 3;
    private static final int PARTS = 4;
    private static final char SPACE = ' ';
    private static final char INSERT = '+';
    private static final char DELETE = '-';
    private static final String OPENING = "<<";
    private static final String CLOSING = ">>";

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
                String signedIndent = lineArray[INDENTS_PART];
                char sign = signedIndent.length() == 2 ? signedIndent.charAt(0) : SPACE;
                int numberOfIndents = Integer.parseInt(String.valueOf(signedIndent.length() == 2 ? signedIndent.charAt(1) : signedIndent.charAt(0)));
                XTestCompositeNode newNode = createNode(lineArray, sign, lineNumber);
                nodePerIndex.put(numberOfIndents, newNode);
                nodePerIndex.get(numberOfIndents - 1).addChild(newNode);
            }
            return nodePerIndex.get(-1);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Could not parse %s to a node", lines), e);
        }
    }

    public static XTestCompositeNode convertDiffStringsToNode(List<String> lines) {
        // lines preprocessed to contain "<<'sign'" at the beginning only and ">>" at the end only
        preprocessing(lines);
        List<String> signedLines = addSignsPerLine(lines);
        return convertStringsToNode(signedLines);
    }

    private static void preprocessing(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            if (currentLine.endsWith("<<+") || currentLine.endsWith("<<-")) {
                // add the sign to the next line
                lines.set(i + 1, currentLine.substring(currentLine.length() - 3) + lines.get(i + 1));
                // remove it from current line
                lines.set(i, currentLine.substring(0, currentLine.length() - 3));
            }
            if (currentLine.startsWith(">>")) {
                // remove sign from current line
                lines.set(i, currentLine.substring(2));
                // add sign to previous line
                lines.set(i - 1, lines.get(i - 1) + ">>");
            }
        }
    }

    private static List<String> addSignsPerLine(List<String> lines) {
        List<String> signedLines = new ArrayList<>();
        boolean isInserting = false;
        boolean isDeleting = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if ((!line.contains("<<") && !line.contains(">>") && !isInserting && !isDeleting)) {
                signedLines.add(line);
                continue;
            } else if (line.contains(">><<")) {
                String[] values = StringUtils.substringsBetween(line, "<<", ">>");
                String lineWithNoValue = line.substring(0, line.indexOf("<<"));
                signedLines.add('-' + lineWithNoValue + values[0].substring(1));
                signedLines.add('+' + lineWithNoValue + values[1].substring(1));
            } else {
                if (line.startsWith("<<+")) {
                    isInserting = true;
                    signedLines.add('+' + line.substring(3));
                } else if (isInserting) {
                    signedLines.add('+' + line);
                }

                if (line.startsWith("<<-")) {
                    isDeleting = true;
                    signedLines.add('-' + line.substring(3));
                } else if (isDeleting) {
                    signedLines.add('-' + line);
                }

                if ((isInserting || isDeleting) && line.endsWith(">>")) {
                    int lastIndex = signedLines.size() - 1;
                    String currentSignedLine = signedLines.get(lastIndex);
                    signedLines.set(lastIndex, currentSignedLine.substring(0, currentSignedLine.length() - 2));
                    isInserting = false;
                    isDeleting = false;
                }
            }
        }
        return signedLines;
    }

    @NotNull
    private static XTestCompositeNode createDummyNode() {
        return createNode(new String[] {"", "name", "nodeId", "value"}, SPACE, -1);
    }

    private static XTestCompositeNode createNode(String[] next, char signOrSpace, int lineNumber) {
        return XTestCompositeNode.createNode(next[NAME_PART], next[TYPE_PART], next[VALUE_PART], signOrSpace, lineNumber);
    }

    public static List<String> convertNodeToStrings(XTestCompositeNode node) {
        StringBuilder sb = new StringBuilder();
        addLines(sb, node.getChildren(), 0);
        return Arrays.stream(sb.toString().split("\n")).collect(Collectors.toList());
    }

    private static void addLines(StringBuilder sb, List<XTestCompositeNode> nodes, int indents) {
        nodes.forEach(child -> {
            appendData(sb, child, indents);
            addLines(sb, child.getChildren(), indents + 1);
        });
    }

    private static void appendData(StringBuilder sb, XTestCompositeNode node, int indents) {
        sb.append(indents) //
            .append(",") //
            .append(node.getContainer().toString()) //
            .append(",") //
            .append(node.getNodeId()) //
            .append(",") //
            .append(node.getValue()) //
            .append("\n");
    }

    /**
     * not needed here, to be moved
     */

    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
        LOG.info(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }
}