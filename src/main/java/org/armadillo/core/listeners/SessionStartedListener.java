package org.armadillo.core.listeners;

import org.armadillo.core.components.handlers.ReachServices;
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
