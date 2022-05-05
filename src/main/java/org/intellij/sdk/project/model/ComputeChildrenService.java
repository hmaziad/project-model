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
        Queue<XValueContainer> queue = new LinkedList<>();
        queue.add(container);
        int depth = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                XValueContainer current = queue.poll();
                CompletableFuture<List<XValue>> nodeFuture = new CompletableFuture<>();
                XTestCompositeNode node = new XTestCompositeNode(nodeFuture,queue);
                current.computeChildren(node);
//                List<XValue> children = new ArrayList<>();
                try { nodeFuture.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
                ArrayList<XValueContainer> xValueContainers = new ArrayList<>(queue);
                System.out.println(Thread.currentThread().getName() + ": " + xValueContainers);
                for (XValueContainer child2 : xValueContainers) {
                    XValue child = (XValue) child2;
                    if (!child.toString().equals("hash") && !child.toString().equals("coder") && !child.toString().equals("value")) {
                        CompletableFuture valueFuture = new CompletableFuture();
                        XTestValueNode valueNode = new XTestValueNode(valueFuture);
                        child.computePresentation(valueNode, XValuePlace.TREE);
                        valueFuture.join();
//                        queue.add(child);
                    }
                }

            }
            System.out.println("Queue size outside loop: " + queue.size() + "," + depth++);
        }
    }

}
