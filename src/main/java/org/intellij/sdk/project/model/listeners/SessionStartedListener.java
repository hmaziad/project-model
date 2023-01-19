package org.intellij.sdk.project.model.listeners;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;

public class SessionStartedListener implements DebuggerManagerListener, ReachServices {

    @Override
    public void sessionAttached(DebuggerSession session) {
        COMPONENT_SERVICE.getSnapIsEnabled().set(true);
    }

    @Override
    public void sessionDetached(DebuggerSession session) {
        COMPONENT_SERVICE.getSnapIsEnabled().set(false);
    }

}
