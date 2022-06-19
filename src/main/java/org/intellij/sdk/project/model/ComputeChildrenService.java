package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.intellij.sdk.project.model.xnodes.XTestValueNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueContainer;
import com.intellij.xdebugger.frame.XValuePlace;

import groovy.util.logging.Slf4j;

@Slf4j
public class ComputeChildrenService {
    private final XValueContainer container;

    public ComputeChildrenService(XValueContainer container) {
        this.container = container;
    }

    public void execute() {
        computeChildren(container);
    }

    private void computeChildren(XValueContainer container) {
        Queue<XTestCompositeNode> queue = new LinkedList<>();
        Set<Integer> calculateChildrenIds = new HashSet<>();
        XTestCompositeNode rootComposite = new XTestCompositeNode(queue, container);
        queue.add(rootComposite);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                XTestCompositeNode current = queue.poll();
                current.retrieveNodeIdAndRef();
                if (current.getRef() != 0 && calculateChildrenIds.contains(current.getRef())) {
                    continue;
                }
                current.getContainer().computeChildren(current);
                current.getFuture().join();
                current.retrieveNodeIdAndRef();
                calculateChildrenIds.add(current.getRef());
                List<XTestCompositeNode> childrenContainers = new ArrayList<>(queue);
                for (XTestCompositeNode childCompositeNode : childrenContainers) {
                    XValue child = (XValue) childCompositeNode.getContainer();
                    CompletableFuture valueFuture = new CompletableFuture();
                    XTestValueNode valueNode = new XTestValueNode(valueFuture, childCompositeNode, childCompositeNode.getFuture());
                    child.computePresentation(valueNode, XValuePlace.TREE);
                    valueFuture.join();
                }
            }
        }

        print(rootComposite);
    }

    private void print(XTestCompositeNode node) {
        print(node, "");
    }

    private void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().stream().forEach(child -> print(child, tab + "\t"));
    }
}
