package org.intellij.sdk.project.model.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

public class DiffServiceTest {
    @Test
    public void test_diff_two_strings() throws IOException {
        List<String> original = Files.readAllLines(Path.of("src/test/resources/nodeAsString/source-numbers.txt"));
        List<String> revised = Files.readAllLines(Path.of("src/test/resources/nodeAsString/destination-numbers.txt"));

        List<String> expectedDiff = Files.readAllLines(Path.of("src/test/resources/nodeAsString/diff-result-numbers.txt"));
        List<String> actualDiff = DiffService.diffStrings(original, revised);

        assertEquals(expectedDiff.size(), actualDiff.size());
        IntStream //
            .range(0, expectedDiff.size()) //
            .forEach(index -> assertEquals(expectedDiff.get(index), actualDiff.get(index)));
    }

    @Test
    public void test_diff_two_strings_corner_case_1_success() throws IOException {
        /**
         * TODO BE TESTED, revert Dual Line, apply same test, there will be an error in parsing
         */
        List<String> original = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/original.txt"));
        List<String> revised = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/revised.txt"));

        List<String> expectedDiff = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/expected-diff.txt"));
        List<String> actualDiff = DiffService.diffStrings(original, revised);
        assertEquals(expectedDiff.size(), actualDiff.size());
        IntStream //
            .range(0, expectedDiff.size()) //
            .forEach(index -> assertEquals(expectedDiff.get(index), actualDiff.get(index)));
    }

    @Test
    @Ignore("The diff tool is random in this case")
    public void test_diff_two_strings_corner_case_1_success_fail() throws IOException {
        /**
         * TODO BE TESTED, revert Dual Line, apply same test, there will be an error in parsing
         */
        List<String> original = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/original.txt"));
        List<String> revised = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/revised.txt"));

        List<String> expectedDiff = Files.readAllLines(Path.of("src/test/resources/nodeAsString/corner-case-1/expected-diff.txt"));
        List<String> actualDiff = DiffService.diffStrings(original, revised, (short) 32);
        assertEquals(expectedDiff.size(), actualDiff.size());
        IntStream //
            .range(0, expectedDiff.size()) //
            .forEach(index -> assertEquals(expectedDiff.get(index), actualDiff.get(index)));
    }
}