package org.intellij.sdk.project.model;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.intellij.sdk.project.model.xnodes.XTestValueNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueContainer;
import com.intellij.xdebugger.frame.XValuePlace;

import lombok.extern.slf4j.Slf4j;

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
        Queue<XTestCompositeNode> nodesQueue = new ArrayDeque<>();
        Set<Integer> childrenRefs = new HashSet<>();
        XTestCompositeNode rootNode = new XTestCompositeNode(nodesQueue, container);
        nodesQueue.add(rootNode);

        while (!nodesQueue.isEmpty()) {
            int nodesQueueSize = nodesQueue.size();
            for (int i = 0; i < nodesQueueSize; i++) {
                XTestCompositeNode currentNode = nodesQueue.poll();
                Objects.requireNonNull(currentNode, "Pulled node from Queue is null").retrieveNodeIdAndRef();
                // checks for loops in graph
                if (currentNode.getRef() != 0 && childrenRefs.contains(currentNode.getRef())) {
                    continue;
                }
                computeChildren(currentNode);
                childrenRefs.add(currentNode.getRef());
                for (XTestCompositeNode childNode : nodesQueue) {
                    XValue childValue = (XValue) childNode.getContainer();
                    CompletableFuture noChildrenFuture = new CompletableFuture();
                    XTestValueNode valueNode = new XTestValueNode(noChildrenFuture, childNode.getFuture(), childNode);
                    childValue.computePresentation(valueNode, XValuePlace.TREE);
                    noChildrenFuture.join();
                }
            }
        }
        log.info("Finished Calculating Children");
        print(rootNode);
    }

    private void computeChildren(XTestCompositeNode currentNode) {
        currentNode.getContainer().computeChildren(currentNode);
        currentNode.getFuture().join();
        currentNode.retrieveNodeIdAndRef();
    }

    private void print(XTestCompositeNode node) {
        print(node, "");
    }

    private void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.getContainer().toString() + " " + node.getNodeId() + " " + node.getValue());
        node.getChildren().forEach(child -> print(child, tab + "\t"));
    }
}
