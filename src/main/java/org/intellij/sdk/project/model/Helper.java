package org.intellij.sdk.project.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

public class Helper {
    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }

    public static XTestCompositeNode unifiedDiff(String basePath, String targetPath) {
        DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true).inlineDiffByWord(true).oldTag(f -> "").newTag(f -> "").build();

        List<String> original = null;
        List<String> revised = null;
        try {
            original = Files.readAllLines(Paths.get(basePath));
            revised = Files.readAllLines(Paths.get(targetPath));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
        return Parser.parse(output);
    }

}
