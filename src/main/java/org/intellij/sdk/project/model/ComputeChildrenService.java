package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueContainer;
import com.intellij.xdebugger.frame.XValuePlace;

public class ComputeChildrenService implements Task {
    private final XValueContainer container;

    public ComputeChildrenService(XValueContainer container) {
        this.container = container;
    }

    @Override
    public void execute() {
        computeChildren(container);
    }

    private void computeChildren(XValueContainer container) {
        CompletableFuture<XValueChildrenList> cf = new CompletableFuture<>();
        XTestCompositeNode node = new XTestCompositeNode(cf);
        container.computeChildren(node);
        List<XValue> children = getChildren(cf.join());
        System.out.println(Thread.currentThread().getName() + ": " + children);

        for (var child : children) {
            if (!child.toString().equals("hash") && !child.toString().equals("coder") && !child.toString().equals("value")) {
                CompletableFuture.runAsync(() -> computeChildren(child));
            }
        }
    }


    private List<XValue> getChildren(XValueChildrenList children) {
        List<XValue> values = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            XValue value = children.getValue(i);
            value.computePresentation(new XTestValueNode(), XValuePlace.TREE);
            values.add(value);
        }
        return values;
    }
}
