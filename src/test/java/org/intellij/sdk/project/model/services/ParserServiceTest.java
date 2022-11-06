package org.intellij.sdk.project.model.services;

import static org.intellij.sdk.project.model.services.ParserService.convertDiffStringsToNode;
import static org.intellij.sdk.project.model.services.ParserService.convertNodeToStrings;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.junit.Test;
import com.intellij.xdebugger.frame.XValueContainer;

public class ParserServiceTest {

    @Test
    public void test_parse_normal_strings_to_node_dfs() throws IOException {
        assertParsingForFile("src/test/resources/nodeAsString/source-numbers.txt");
        assertParsingForFile("src/test/resources/nodeAsString/destination-numbers.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_parse_corrupted_string_handled() throws IOException {
        assertParsingForFile("src/test/resources/nodeAsString/corrupted-strings.txt");
    }


    private void assertParsingForFile(String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        XTestCompositeNode rootNode = ParserService.convertStringsToNode(lines);
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

    @Test
    public void test_convert_node_to_strings() throws IOException {
        XValueContainer container = getContainer();
        XTestCompositeNode rootNode = new XTestCompositeNode(container, "rootNode_value", "rootNode_nodeId");
        XTestCompositeNode childAA = new XTestCompositeNode(container, "childAA_value", "childAA_nodeId");
        XTestCompositeNode childA1 = new XTestCompositeNode(container, "childA1_value", "childA1_nodeId");
        XTestCompositeNode childA2 = new XTestCompositeNode(container, "childA2_value", "childA2_nodeId");
        XTestCompositeNode childA3 = new XTestCompositeNode(container, "childA3_value", "childA3_nodeId");
        XTestCompositeNode childBB = new XTestCompositeNode(container, "childBB_value", "childBB_nodeId");
        XTestCompositeNode childB1 = new XTestCompositeNode(container, "childB1_value", "childB1_nodeId");
        XTestCompositeNode childB2 = new XTestCompositeNode(container, "childB2_value", "childB2_nodeId");
        XTestCompositeNode childB3 = new XTestCompositeNode(container, "childB3_value", "childB3_nodeId");
        rootNode.setChildren(List.of(childAA, childBB));
        childAA.setChildren(List.of(childA1, childA2, childA3));
        childBB.setChildren(List.of(childB1, childB2, childB3));

        List<String> expectedStrings = Files.readAllLines(Path.of("src/test/resources/nodeAsString/expected-converted-node.txt"));
        List<String> actualStrings = convertNodeToStrings(rootNode);

        assertEquals(expectedStrings.size(), actualStrings.size());
        IntStream //
            .range(0, expectedStrings.size()) //
            .forEach(index -> assertEquals(expectedStrings.get(index), actualStrings.get(index)));
    }

    private XValueContainer getContainer() {
        return new XValueContainer() {
            @Override
            public String toString() {
                return "container";
            }
        };
    }

    @Test
    public void test_parse_diff_strings_to_node_dfs() throws IOException {
        // todo complete this test
        List<String> lines = Files.readAllLines(Path.of("src/test/resources/nodeAsString/diff-result-numbers.txt"));
        convertDiffStringsToNode(lines);
    }
}