package org.intellij.sdk.project.model;

import static org.intellij.sdk.project.model.XDebuggerTestUtil.print;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.frame.XValueContainer;
public class MyPostStartupActivity implements StartupActivity, StartupActivity.DumbAware {

    ExecutorService service = Executors.newFixedThreadPool(2);

    @Override
    public void runActivity(@NotNull Project project) {
        attachDebugStartListener(project);
    }

    private void attachDebugStartListener(Project project) {
        project.getMessageBus().connect().subscribe(XDebuggerManager.TOPIC, new XDebuggerManagerListener() {
            @Override
            public void processStarted(@NotNull XDebugProcess debugProcess) {
                print("Project Started Debug Process");
                attachDebugBreakListener(debugProcess);
            }
        });
    }

    private void attachDebugBreakListener(@NotNull XDebugProcess debugProcess) {
        debugProcess.getSession().addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                print("Session Paused");
                service.submit(() -> traverseChildren(debugProcess.getSession().getCurrentStackFrame()));
            }
            @Override
            public void sessionResumed() {
                print("Session Resumed");
            }

            @Override
            public void sessionStopped() {
                print("Session Stopped");
            }

            @Override
            public void stackFrameChanged() {
                print("StackFrame Changed");
            }
        });
    }

    private void traverseChildren(XValueContainer nodeFromDebugger) {

        XTestCompositeNode container = new XTestCompositeNode(); // create semaphore
        print("computing children..."+nodeFromDebugger);
        nodeFromDebugger.computeChildren(container);
        var output = container.waitFor(25_000, this::waitFor);

        print("Children :"+ output.first);
        for (XValue child : output.first) {
            traverseChildren(child);
        }
    }

    public boolean waitFor(Semaphore semaphore, long timeoutInMillis) {
        long remaining = timeoutInMillis;
        try {
            print("Trying to acquire: " + semaphore);
            boolean isAcquired = semaphore.tryAcquire(remaining, TimeUnit.MILLISECONDS);
            print("Has acquired: " + semaphore + ": " + isAcquired);
            return isAcquired;
        } catch (InterruptedException ignored) {
            print("Interrupted" + ignored);
        }
        return false;
    }
}