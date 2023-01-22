package org.intellij.sdk.project.model.components.handlers;

import org.intellij.sdk.project.model.services.ComponentService;
import org.intellij.sdk.project.model.services.PersistencyService;
import com.intellij.openapi.components.ServiceManager;

public interface ReachServices {
    ComponentService COMPONENT_SERVICE = ServiceManager.getService(ComponentService.class);
    PersistencyService PERSISTENCY_SERVICE = ServiceManager.getService(PersistencyService.class);
}
