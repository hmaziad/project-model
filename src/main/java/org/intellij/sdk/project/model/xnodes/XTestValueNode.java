/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.sdk.project.model.xnodes;

import java.util.List;
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
    private final CompletableFuture childNodeFuture;
    private XTestCompositeNode compositeNode;
    private CompletableFuture<List<XValue>> noChildrenFuture;
    private @NotNull String myValue;

    public XTestValueNode(CompletableFuture childNodeFuture, CompletableFuture<List<XValue>> noChildrenFuture, XTestCompositeNode compositeNode) {
        this.childNodeFuture = childNodeFuture;
        this.noChildrenFuture = noChildrenFuture;
        this.compositeNode = compositeNode;
    }

    @Override
    public void applyPresentation(@Nullable Icon icon, @NotNull XValuePresentation valuePresentation, boolean hasChildren) {
        myValue = XValuePresentationUtil.computeValueText(valuePresentation);
        if (!myValue.startsWith("Collecting data")) {
            compositeNode.setValue(myValue);
            childNodeFuture.complete(null);
        }
        if (!hasChildren) {
            noChildrenFuture.complete(null);
        }
    }

    @Override
    public void setFullValueEvaluator(@NotNull XFullValueEvaluator fullValueEvaluator) {
    // do nothing
    }
}
