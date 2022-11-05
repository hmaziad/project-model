package org.intellij.sdk.project.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class DiffService {
    public static List<String> diffStrings(List<String> original, List<String> revised) {
        diff_match_patch dmp = new diff_match_patch(new StandardBreakScorer());
        String originalString = String.join("\n", original);
        String revisedString = String.join("\n", revised);
        LinkedList<diff_match_patch.Diff> diff = dmp.diff_main(originalString, revisedString);
        StringBuilder sb = new StringBuilder();
        diff.forEach(item -> {
                if (item.operation == diff_match_patch.Operation.INSERT) {
                    appendWithSign(sb, item, "+");
                } else if (item.operation == diff_match_patch.Operation.DELETE) {
                    appendWithSign(sb, item, "-");
                } else {
                    sb.append(item.text);
                }
            }
        );
        return Arrays.asList(sb.toString().split("\n"));
    }

    private static void appendWithSign(StringBuilder sb, diff_match_patch.Diff item, String sign) {
        sb.append("<<").append(sign).append(item.text).append(">>");
    }
}