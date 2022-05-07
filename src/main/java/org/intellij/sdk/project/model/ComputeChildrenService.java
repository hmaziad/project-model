package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.intellij.xdebugger.frame.XValue;
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
        System.out.println("Inside Thread: " + Thread.currentThread().getName());
        Queue<XTestCompositeNode> queue = new LinkedList<>();
        XTestCompositeNode rootComposite = new XTestCompositeNode(queue, container);
        queue.add(rootComposite);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                XTestCompositeNode current = queue.poll();
                CompletableFuture nodeFuture = new CompletableFuture<>();
//                XTestCompositeNode node = new XTestCompositeNode(nodeFuture, queue, current.toString());
                current.future = nodeFuture;
                current.container.computeChildren(current);
                waitForResolving(nodeFuture);
                ArrayList<XTestCompositeNode> childrenContainers = new ArrayList<>(queue);
                System.out.println(Thread.currentThread().getName() + ": " + childrenContainers);
                for (XTestCompositeNode child2 : childrenContainers) {
                    XValue child = (XValue) child2.container;
                    if (!child.toString().equals("hash") && !child.toString().equals("coder") && !child.toString().equals("value")) {
                        CompletableFuture valueFuture = new CompletableFuture();
                        XTestValueNode valueNode = new XTestValueNode(valueFuture, child2);
                        child.computePresentation(valueNode, XValuePlace.TREE);
                        valueFuture.join();
                    }
                }
            }
            System.out.println("Queue size outside loop: " + queue.size() + "," + depth++);
        }

        System.out.println("Completed Everything");

        print(rootComposite, "");
    }

    private void print(XTestCompositeNode node, String tab) {
        System.out.println(tab + node.container.toString() +": " + node.value);
        node.children.stream().forEach(child -> print(child,tab + "\t"));
    }

    private void waitForResolving(CompletableFuture<List<XValue>> nodeFuture) {
        try {
            nodeFuture.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
