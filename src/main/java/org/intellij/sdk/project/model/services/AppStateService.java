package org.intellij.sdk.project.model.services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import lombok.extern.log4j.Log4j2;

@State(
    name = "AppServiceState", storages = {
    @Storage("AppServiceStorage.xml")
})
@Log4j2
public class AppStateService implements PersistentStateComponent<AppStateService> {

    public void loadState(AppStateService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public AppStateService getState() {
        return this;
    }

}
