package org.armadillo.core.listeners;

import org.armadillo.core.components.handlers.TreeHandler;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.ide.ActivityTracker;
import com.intellij.xdebugger.XDebugSessionListener;

public class MyXDebugSessionListener implements XDebugSessionListener {

    private final TreeHandler treeHandler;
    private final DebuggerSession session;

    public MyXDebugSessionListener(TreeHandler treeHandler, DebuggerSession session) {
        this.treeHandler = treeHandler;
        this.session = session;
    }

    @Override
    public void sessionPaused() {
        this.treeHandler.setSnapEnabled(true, this.session);
        ActivityTracker.getInstance().inc();
    }

    @Override
    public void sessionResumed() {
        treeHandler.setSnapEnabled(false, session);
        ActivityTracker.getInstance().inc();
    }
}
