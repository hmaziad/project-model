// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnapDebugger extends AnAction implements ToolWindowFactory {

    private static final String PROJECT_NOT_FOUND_ERROR_MESSAGE = "Please open a project to use this feature";
    private static final String START_DEBUGGER_ERROR_MESSAGE = "Please start debugger to use this feature";
    private static ComputeChildrenService computeChildrenService = new ComputeChildrenService();

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        Project project = Objects.requireNonNull(event.getProject(), PROJECT_NOT_FOUND_ERROR_MESSAGE);
        log.info("Project {}...", project);
        XDebuggerManager manager = XDebuggerManager.getInstance(project);
        XDebugSession currentSession = manager.getCurrentSession();
        XDebugSession session = Objects.requireNonNull(currentSession, START_DEBUGGER_ERROR_MESSAGE);
        log.info("Debug Session Retrieved...");
        this.computeChildrenService.initStackFrame(session.getCurrentStackFrame());
        log.info("Start Computing Children...");
        CompletableFuture.runAsync(computeChildrenService::execute);
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MyToolWindow myToolWindow = new MyToolWindow(project);
        this.computeChildrenService.initToolWindow(myToolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), "Debugger Tab 1", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        boolean visibility = Objects.nonNull(event.getProject());
        event.getPresentation().setEnabled(visibility);
        event.getPresentation().setVisible(visibility);
    }

}
