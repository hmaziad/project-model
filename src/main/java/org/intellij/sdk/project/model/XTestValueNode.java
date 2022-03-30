/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package org.intellij.sdk.project.model;

import com.intellij.xdebugger.frame.XFullValueEvaluator;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodePresentationConfigurator;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class XTestValueNode extends XValueNodePresentationConfigurator.ConfigurableXValueNodeImpl {
  public @Nullable Icon myIcon;
  public @NotNull String myName;
  public @Nullable String myType;
  public @NotNull String myValue;
  public boolean myHasChildren;
  public XFullValueEvaluator myFullValueEvaluator;

  @Override
  public void applyPresentation(@Nullable Icon icon,
                                @NotNull XValuePresentation valuePresentation,
                                boolean hasChildren) {
    myIcon = icon;
    myType = valuePresentation.getType();
    myValue = XValuePresentationUtil.computeValueText(valuePresentation);
    myHasChildren = hasChildren;

      System.out.println("Presentation: " + this);
  }

  @Override
  public void setFullValueEvaluator(@NotNull XFullValueEvaluator fullValueEvaluator) {
    myFullValueEvaluator = fullValueEvaluator;
  }

  @Override
  public String toString() {
    return myName + "{" + myType + "} = " + myValue + ", hasChildren = " + myHasChildren;
  }
}
