package org.intellij.sdk.project.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;

public class Parser {
    private static final int INDEX = 0;
    private static final String ZERO = "0";
    private static final int NAME = 1;
    private static final int TYPE = 2;
    private static final int VALUE = 3;

    public static void main(String[] args) throws URISyntaxException, IOException {
        Path path = Paths.get("D:\\sno\\plugin-development\\project-model\\schema.txt");
        List<String> lines = Files.readAllLines(path);
        List<String[]> lineArrays = new ArrayList<>();
        for (String line : lines) {
            String[] lineArray = line.split(",", 4);
            lineArray[0] = String.valueOf(lineArray[0].length());
            lineArrays.add(lineArray);
        }

        Map<Integer, XTestCompositeNode> nodePerIndex = new HashMap<>();
        for (var lineArray : lineArrays) {
            int index = Integer.parseInt(lineArray[INDEX]);
            XTestCompositeNode newNode = createNode(lineArray);
            nodePerIndex.put(index, newNode);
            if (index == 0) {
                continue;
            }
            nodePerIndex.get(index - 1).addChild(newNode);
        }
        var rootNode = nodePerIndex.get(0);
        print(rootNode);
    }

    private static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
//        System.out.println(tab + node.name + " " + node.type + " " + node.value);
//        node.children.stream().forEach(child -> print(child, tab + "\t"));
    }


    private static XTestCompositeNode createNode(String[] next) {
//        return new XTestCompositeNode(next[NAME], next[TYPE], next[VALUE]);
        return null;
    }


}
