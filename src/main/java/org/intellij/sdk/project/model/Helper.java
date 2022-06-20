package org.intellij.sdk.project.model;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;

public class Helper {
    public static void print(XTestCompositeNode node) {
        print(node, "");
    }

    private static void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }
}
