// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnapDebugger extends AnAction {

    private static final String PROJECT_NOT_FOUND_ERROR_MESSAGE = "Please open a project to use this feature";
    private static final String START_DEBUGGER_ERROR_MESSAGE = "Please start debugger to use this feature";

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        Project project = Objects.requireNonNull(event.getProject(), PROJECT_NOT_FOUND_ERROR_MESSAGE);
        XDebuggerManager manager = XDebuggerManager.getInstance(project);
        XDebugSession currentSession = manager.getCurrentSession();
        XDebugSession session = Objects.requireNonNull(currentSession, START_DEBUGGER_ERROR_MESSAGE);
        ComputeChildrenService computeChildrenService = new ComputeChildrenService(session.getCurrentStackFrame());
        CompletableFuture.runAsync(computeChildrenService::execute);
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        boolean visibility = Objects.nonNull(event.getProject());
        event.getPresentation().setEnabled(visibility);
        event.getPresentation().setVisible(visibility);
    }

}
