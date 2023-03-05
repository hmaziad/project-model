// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.armadillo.core;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.tree.components.DebugTreeManager;
import org.armadillo.core.components.toolbar.DebuggerWindowContent;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class DebugWindow implements ReachServices {

    private JPanel debuggerWindowContent;

    public DebugWindow(Project project) {
        this.debuggerWindowContent = new DebuggerWindowContent(true);
        DebugTreeManager debugTreeManager = treeHandler.getDebugTreeManager(project);
        debugTreeManager.addClearButtonListener();
        JTree debugTree = debugTreeManager.getDebugTree();
        JScrollPane scrollPane = getScrollPane(debugTree);
        this.debuggerWindowContent.add(scrollPane);
    }

    @NotNull
    private JScrollPane getScrollPane(JTree debugTree) {
        JPanel panel = new JPanel(new BorderLayout());
        EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);
        panel.setBorder(emptyBorder);
        panel.add(debugTree);
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setBorder(emptyBorder);
        scrollPane.setViewportView(panel);
        return scrollPane;
    }
}