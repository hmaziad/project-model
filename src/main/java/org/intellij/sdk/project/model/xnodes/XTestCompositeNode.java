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
package org.intellij.sdk.project.model.xnodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import org.jetbrains.annotations.NotNull;
import com.intellij.debugger.engine.JavaValue;
import com.intellij.debugger.ui.impl.watch.ValueDescriptorImpl;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueContainer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Collecting data\u2026
@Getter
@Setter
@NoArgsConstructor
public class XTestCompositeNode extends XTestContainer<XValue> implements XCompositeNode {
    private CompletableFuture<List<XValue>> future = new CompletableFuture<>();
    private Queue<XTestCompositeNode> queue;
    private XValueContainer container;
    private String value;
    private List<XTestCompositeNode> children = new ArrayList<>();
    private String nodeId = "";
    private int ref;
    private char diffChar;
    private int lineNumber;
    private Icon icon;

    public XTestCompositeNode(Queue<XTestCompositeNode> queue, XValueContainer container) {
        this.queue = queue;
        this.container = container;
    }

    public static XTestCompositeNode createNode(String name, String nodeId, String value, char signOrSpace, int lineNumber) {
        XTestCompositeNode node = new XTestCompositeNode(null, null);
        node.setContainer(new XValueContainer() {
            @Override
            public String toString() {
                return name;
            }
        });
        node.setNodeId(nodeId);
        node.setValue(value);
        node.setDiffChar(signOrSpace);
        node.setLineNumber(lineNumber);
        return node;
    }

    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        addChildrenToQueue(queue, children);
        if (last) {
            this.future.complete(null);
        }
    }

    private void addChildrenToQueue(Queue<XTestCompositeNode> queue, XValueChildrenList children) {
        for (var child : getChildren(children)) {
            var isNotStringUselessChildren = !child.toString().equals("hash") && !child.toString().equals("coder") && !child.toString().equals("value");
            if (isNotStringUselessChildren) {
                XTestCompositeNode childComposite = new XTestCompositeNode(queue, child);
                addChild(childComposite);
                queue.add(childComposite);
            }
        }
    }

    public void addChild(XTestCompositeNode child) {
        this.children.add(child);
        add(child);
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

    public void retrieveNodeIdAndRef() {
        if (container instanceof JavaValue) {
            ValueDescriptorImpl descriptor = ((JavaValue) container).getDescriptor();
            if (descriptor.getIdLabel() != null) {
                nodeId = descriptor.getIdLabel();
            }
            if (descriptor.getValue() != null) {
                this.ref = descriptor.getValue().hashCode();
            }
        }
    }

    @Override
    public String toString() {
        String myNodeId = getNodeId().isEmpty() ? "" : String.format("{%s}", getNodeId());
        return getContainer().toString() + " = " + myNodeId + " " + getValue();
    }

    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
