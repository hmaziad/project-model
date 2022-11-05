package org.intellij.sdk.project.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.junit.Test;

public class ParserServiceTest {

    @Test
    public void test_parse_strings_to_node_dfs() throws IOException {
        assertParsingForFile("src/test/resources/nodeAsString/source.txt");
        assertParsingForFile("src/test/resources/nodeAsString/destination.txt");
    }

    private void assertParsingForFile(String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        XTestCompositeNode rootNode = ParserService.parseStringsToNode(lines);
        assertNodeValues(rootNode.getChildren(), lines, new AtomicInteger(0));
    }

    private void assertNodeValues(List<XTestCompositeNode> children, List<String> lines, AtomicInteger index) {
        if (CollectionUtils.isNotEmpty(children)) {
            for (var currentNode : children) {
                String errorMessage = String.format("Current node of value %s does not equal %s", currentNode.getValue(), lines.get(index.get()));
                String[] values = lines.get(index.getAndIncrement()).split(",");
                assertEquals(errorMessage, currentNode.getContainer().toString(), values[1]);
                assertEquals(errorMessage, currentNode.getNodeId(), values[2]);
                assertEquals(errorMessage, currentNode.getValue(), values[3]);
                assertNodeValues(currentNode.getChildren(), lines, index);
            }
        }
    }
}