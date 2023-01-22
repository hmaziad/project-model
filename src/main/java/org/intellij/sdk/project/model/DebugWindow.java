// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import javax.swing.*;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.components.toolbar.DebuggerWindowContent;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class DebugWindow implements ReachServices {

    private JPanel debuggerWindowContent;

    public DebugWindow() {
        this.debuggerWindowContent = new DebuggerWindowContent(true);
        DebugTreeManager debugTreeManager = COMPONENT_SERVICE.getDebugTreeManager();
        debugTreeManager.addClearButtonListener();
        JTree debugTree = debugTreeManager.getDebugTree();
        this.debuggerWindowContent.add(debugTree);
    }
}