// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XStackFrame;

public class SnapDebugger extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        XDebuggerManager manager = XDebuggerManager.getInstance(event.getProject());
        XStackFrame frame = manager.getCurrentSession().getCurrentStackFrame();
        CompletableFuture.runAsync(() -> new ComputeChildrenService(frame).execute());
        System.out.println(Thread.currentThread().getName() + ": Done");
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        boolean visibility = event.getProject() != null;
        event.getPresentation().setEnabled(visibility);
        event.getPresentation().setVisible(visibility);
    }

}
