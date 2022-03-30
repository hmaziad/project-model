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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValuePlace;
// Collecting data\u2026
public class XTestCompositeNode extends XTestContainer<XValue> implements XCompositeNode {
    Map<XTestCompositeNode, XTestValueNode> valuePerNode = new HashMap<>();
    @Override
    public void addChildren(@NotNull XValueChildrenList children, boolean last) {
        for (int i = 0; i < children.size(); i++) {
            XValue childValue = children.getValue(i);

            if(!(childValue.toString().equals("value")) && !(childValue.toString().equals("hash")) && !(childValue.toString().equals("coder"))) {

                XTestCompositeNode childNode = new XTestCompositeNode();
                childValue.computeChildren(childNode);

                XTestValueNode presentation = new XTestValueNode(childValue);
                childValue.computePresentation(presentation, XValuePlace.TREE);
            }
        }
    }

    @Override
    public void setAlreadySorted(boolean alreadySorted) {
    }
}
