// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;


import static org.intellij.sdk.project.model.XDebuggerTestUtil.print;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.ui.impl.watch.NodeManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.attach.XAttachDebuggerProvider;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueContainer;
import com.intellij.xdebugger.impl.XDebuggerManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

public class ShowDebuggers extends AnAction {

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        XDebuggerManager manager = XDebuggerManager.getInstance(event.getProject());
        var frame = manager.getCurrentSession().getCurrentStackFrame();
        try {
            printChildren(frame);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void printChildren(XValueContainer frame) throws ExecutionException, InterruptedException, TimeoutException {
        List<XValue> children = XDebuggerTestUtil.collectChildren(frame);
        print("Received children: "+ children);
//        ExecutorService executor = Executors.newFixedThreadPool(1);

        for (XValue child : children) {
//            Future<XTestValueNode>  valueNodeFuture =executor.submit(() -> XDebuggerTestUtil.computePresentation(child));
//            print("Child: " + child + ", Presentation: " + valueNodeFuture.get(25000, TimeUnit.MILLISECONDS).myValue);
//            print("Child: " + child );
            printChildren(child);
        }
    }

    @Override
    public void update(@NotNull final AnActionEvent event) {
        boolean visibility = event.getProject() != null;
        event.getPresentation().setEnabled(visibility);
        event.getPresentation().setVisible(visibility);
    }

}
