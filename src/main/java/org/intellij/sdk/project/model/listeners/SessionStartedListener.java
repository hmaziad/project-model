package org.intellij.sdk.project.model.listeners;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.ide.ActivityTracker;

public class SessionStartedListener implements DebuggerManagerListener, ReachServices {

    @Override
    public void sessionAttached(DebuggerSession session) {
        treeHandler.setSnapEnabled(true, session.getProject());
        ActivityTracker.getInstance().inc();
    }

    @Override
    public void sessionDetached(DebuggerSession session) {
        treeHandler.setSnapEnabled(false, session.getProject());
        ActivityTracker.getInstance().inc();
    }

}
