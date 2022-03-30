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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValuePlace;
// Collecting data\u2026
public class XTestCompositeNode extends XTestContainer<XValue> implements XCompositeNode {
    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        for (int i = 0; i < children.size(); i++) {
            XTestCompositeNode childrenNode = new XTestCompositeNode();
            XValue value = children.getValue(i);
            XTestValueNode presentation = new XTestValueNode();

            if(!(value.toString().equals("value")) && !(value.toString().equals("hash")) && !(value.toString().equals("coder"))) {
                value.computeChildren(childrenNode);
                value.computePresentation(presentation, XValuePlace.TREE);
                System.out.println("child: "+ value);
            }
        }
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
    }
}
