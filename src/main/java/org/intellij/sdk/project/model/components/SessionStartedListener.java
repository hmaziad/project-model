package org.intellij.sdk.project.model.components;

import org.intellij.sdk.project.model.services.ButtonEnablingService;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.components.ServiceManager;

public class SessionStartedListener implements DebuggerManagerListener {
    private static final ButtonEnablingService buttonEnablingService = ServiceManager.getService(ButtonEnablingService.class);

    @Override
    public void sessionCreated(DebuggerSession session) {
        buttonEnablingService.enableButton();
    }

    @Override
    public void sessionRemoved(DebuggerSession session) {
        buttonEnablingService.disableButton();
    }
}
