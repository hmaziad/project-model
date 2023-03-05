package org.armadillo.core.listeners;

import java.time.LocalDateTime;
import java.util.Objects;

import org.armadillo.core.components.handlers.ReachServices;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.ide.ActivityTracker;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

public class SessionStartedListener implements DebuggerManagerListener, ReachServices {

    @Override
    public void sessionAttached(DebuggerSession session) {

        XDebugSession currentSession = XDebuggerManager.getInstance(session.getProject()).getCurrentSession();
        if (Objects.isNull(currentSession)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        nodeHandler.setFlowDate(now);
        treeHandler.setFlowId(session,now);
        currentSession.addSessionListener(new MyXDebugSessionListener(treeHandler, session));
    }

    @Override
    public void sessionDetached(DebuggerSession session) {
        nodeHandler.setFlowDate(null);
        treeHandler.setSnapEnabled(false, session);
        ActivityTracker.getInstance().inc();
    }

}
