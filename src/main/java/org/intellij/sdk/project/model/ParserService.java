package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;

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
    public static XTestCompositeNode parseStringsToNode(List<String> lines) {
        List<String[]> lineArrays = new ArrayList<>();
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

    public static String diffStrings2() {
        diff_match_patch dmp = new diff_match_patch(new StandardBreakScorer());
        String source = ",args,String[0]@791,[]\n" + ",names,ArrayList@792, size = 4\n" + " ,0,,Hussein\n" + " ,1,,Ali1\n" + " ,2,,Maziad\n" + " ,3,,testing\n"
            + ",someLists,ArrayList@794, size = 2\n" + " ,0,ImmutableCollections$ListN@801, size = 4\n" + "  ,0,Integer@808,1\n" + "  ,1,Integer@809,3\n" + "  ,2,Integer@809,3\n"
            + "  ,3,Integer@810,7\n" + " ,1,ImmutableCollections$SetN@802, size = 3\n" + "  ,0,Integer@810,7\n" + "  ,1,Integer@812,4\n" + "  ,2,Integer@813,5\n";
        String destination = ",args,String[0]@791,[]\n" + ",names,ArrayList@792, size = 5\n" + " ,0,,Hussein\n" + " ,1,,Ali1\n" + " ,2,,Maziad\n" + " ,3,,testing\n"
            + " ,4,,Jello\n" + ",someLists,ArrayList@794, size = 3\n" + " ,0,ImmutableCollections$ListN@801, size = 4\n" + "  ,0,Integer@808,1\n" + "  ,1,Integer@809,3\n"
            + "  ,2,Integer@809,3\n" + "  ,3,Integer@810,7\n" + " ,1,ImmutableCollections$SetN@802, size = 3\n" + "  ,0,Integer@810,7\n" + "  ,1,Integer@812,4\n"
            + "  ,2,Integer@813,5\n" + " ,2,ImmutableCollections$List12@817, size = 1\n" + "  ,0,Integer@822,12332\n";

        LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(source, destination);
        // Result: [(-1, "Hell"), (1, "G"), (0, "o"), (1, "odbye"), (0, " World.")]
        // Result: [(-1, "Hello"), (1, "Goodbye"), (0, " World.")]
        return dmp.diff_toDelta(diff);
    }


    public static List<String> diffStrings(List<String> original, List<String> revised) {
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

/*
dmp.diff_map(source, destination).stream().map(difff -> {
    if (difff.operation == diff_match_patch.Operation.DELETE) {
        return "-" + difff.text;
    } else if (difff.operation == diff_match_patch.Operation.INSERT) {
        return "+" + difff.text;
    } else {
        return difff.text;
    }
}).reduce("", (acc, cur) -> acc + cur);
 */