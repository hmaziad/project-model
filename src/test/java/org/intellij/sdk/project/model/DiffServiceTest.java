package org.intellij.sdk.project.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

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
}