/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import com.intellij.debugger.engine.JavaValue;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueContainer;

// Collecting data\u2026
// here we are in DebuggerManagerThread

public class XTestCompositeNode extends XTestContainer<XValue> implements XCompositeNode {
    CompletableFuture<List<XValue>> future = new CompletableFuture<>();
    private Queue<XTestCompositeNode> queue;
    XValueContainer container;
    String value;
    List<XTestCompositeNode> children = new ArrayList<>();
    String nodeId = "";

    public XTestCompositeNode(Queue<XTestCompositeNode> queue, XValueContainer container) {
        this.queue = queue;
        this.container = container;
    }

    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        System.out.println(Thread.currentThread().getName() + ": " + getChildren(children) + ", is last: " + last);
        addChildrenToQueue(queue, children);
        if (last) {
            this.future.complete(null);
        }
    }

    private void addChildrenToQueue(Queue<XTestCompositeNode> queue, XValueChildrenList children) {
        for (var child : getChildren(children)) {
            if (isNotStringUselessChildren(child)) {
                XTestCompositeNode childComposite = new XTestCompositeNode(queue, child);
                addChild(childComposite);
                queue.add(childComposite);
            }
        }
    }

    private boolean isNotStringUselessChildren(XValue child) {
        return !child.toString().equals("hash") && !child.toString().equals("coder") && !child.toString().equals("value");
    }

    public void addChild(XTestCompositeNode child) {
        this.children.add(child);
    }


    private List<XValue> getChildren(XValueChildrenList children) {
        List<XValue> values = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            XValue value = children.getValue(i);
            values.add(value);
        }
        return values;
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
    }

    public void retrieveNodeId() {
        System.out.println("Entered node id");
        if (container instanceof JavaValue) {
            System.out.println("is instance of java");
            String idLabel = ((JavaValue) container).getDescriptor().getIdLabel();
            System.out.println("label " +  idLabel);
            if (idLabel != null) {
                nodeId =  idLabel;
            }
        }
    }
}
