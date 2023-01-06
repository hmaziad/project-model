package org.intellij.sdk.project.model.services;

import static org.intellij.sdk.project.model.services.ParserService.convertNodeToString;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.junit.Test;

public class ParserServiceTest {

    @Test
    public void test_convert_node_to_strings() throws IOException {
        DebugNode rootNode = new DebugNode("container rootNode_nodeId rootNode_value");
        DebugNode childAA = new DebugNode("container childAA_nodeId childAA_value");
        DebugNode childA1 = new DebugNode("container childA1_nodeId childA1_value");
        DebugNode childA2 = new DebugNode("container childA2_nodeId childA2_value");
        DebugNode childA3 = new DebugNode("container childA3_nodeId childA3_value");
        DebugNode childBB = new DebugNode("container childBB_nodeId childBB_value");
        DebugNode childB1 = new DebugNode("container childB1_nodeId childB1_value");
        DebugNode childB2 = new DebugNode("container childB2_nodeId childB2_value");
        DebugNode childB3 = new DebugNode("container childB3_nodeId childB3_value");
        rootNode.setMyChildren(List.of(childAA, childBB));
        childAA.setMyChildren(List.of(childA1, childA2, childA3));
        childBB.setMyChildren(List.of(childB1, childB2, childB3));

        List<String> expectedStrings = Files.readAllLines(Path.of("src/test/resources/nodeAsString/expected-converted-node.txt"));
        String actualStrings = convertNodeToString(rootNode);
        assertEquals(Strings.join(expectedStrings, '\n'), actualStrings);
    }

}