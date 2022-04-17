package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.List;

public class MyNode {
    String name;
    List<MyNode> children;

    public MyNode(String name) {
        this.name = name;
    }

    public void addChild(MyNode node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(node);
    }
}
