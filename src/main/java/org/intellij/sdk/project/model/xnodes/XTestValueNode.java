/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.sdk.project.model.xnodes;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XFullValueEvaluator;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodePresentationConfigurator;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;

public class XTestValueNode extends XValueNodePresentationConfigurator.ConfigurableXValueNodeImpl {
    private final CompletableFuture future1;
    private XTestCompositeNode compositeNode;
    private CompletableFuture<List<XValue>> future;
    public @Nullable Icon myIcon;
    public @NotNull String myName;
    public @Nullable String myType;
    public @NotNull String myValue;
    public boolean myHasChildren;
    public XFullValueEvaluator myFullValueEvaluator;

    public XTestValueNode(CompletableFuture future1, XTestCompositeNode compositeNode, CompletableFuture<List<XValue>> future) {
        this.future1 = future1;
        this.compositeNode = compositeNode;
        this.future = future;
    }

    @Override
    public void applyPresentation(@Nullable Icon icon, @NotNull XValuePresentation valuePresentation, boolean hasChildren) {
        myIcon = icon;
        myType = valuePresentation.getType();
        myValue = XValuePresentationUtil.computeValueText(valuePresentation);
        myHasChildren = hasChildren;

        if (!myValue.startsWith("Collecting data")) {
            compositeNode.setValue(myValue);
            future1.complete(null);
        }

        if (!hasChildren) {
            future.complete(null);
        }

    }

    @Override
    public void setFullValueEvaluator(@NotNull XFullValueEvaluator fullValueEvaluator) {
        myFullValueEvaluator = fullValueEvaluator;
    }

    @Override
    public String toString() {
        return "{" + Optional.ofNullable(myType).orElse("") + "} " + myValue;
    }
}
