package org.intellij.sdk.project.model.components;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;

public class SessionStartedListener implements DebuggerManagerListener {
    @Override
    public void sessionCreated(DebuggerSession session) {
        // todo link this to snap button enabled
        System.out.println("session created");
    }
}
