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
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueContainer;

// Collecting data\u2026
// here we are in DebuggerManagerThread


public class XTestCompositeNode extends XTestContainer<XValue> implements XCompositeNode {
    private CompletableFuture<List<XValue>> children;
    private Queue<XValueContainer> queue;

    public XTestCompositeNode(CompletableFuture<List<XValue>> children, Queue<XValueContainer> queue) {
        this.children = children;
        this.queue = queue;
    }

    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        System.out.println(Thread.currentThread().getName() + ": " + getChildren(children));
        if (getChildren(children).size() > 0) {
            queue.addAll(getChildren(children));
        }
        if (last) {
            this.children.complete(getChildren(children));
        }
        //        System.out.println(Thread.currentThread().getName() + ", Parent: " + Optional.ofNullable(parent).orElse(null) + ", Children " + getChildren(children) + ", last: " + last);


        //        for (int i = 0; i < children.size(); i++) {
        //            XValue childValue = children.getValue(i);
        //            XValue myChildValue = new MyXValue(childValue);
        //            if (!childValue.toString().equals("hash") && !childValue.toString().equals("coder") && !childValue.toString().equals("value")) {
        //                XTestCompositeNode childNode = new XTestCompositeNode(childValue);
        //                childValue.computeChildren(childNode);
        //                XTestValueNode presentation = new XTestValueNode(childValue);
        //                childValue.computePresentation(presentation, XValuePlace.TREE);
        //            }
        //        }
    }


    private List<XValue> getChildren(XValueChildrenList children) {
        List<XValue> values = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            XValue value = children.getValue(i);
            //            value.computePresentation(new XTestValueNode(), XValuePlace.TREE);
            values.add(value);
        }
        return values;
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
    }
}
