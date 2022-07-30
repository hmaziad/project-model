package org.intellij.sdk.project.model;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
    name = "Test", storages = {
    @Storage("testpersist.xml")
})
public class PersistencyService implements PersistentStateComponent<PersistencyService> {

    String ceva;

    public PersistencyService() {
        ceva = "sad";
        System.out.println("constr");
    }

    public String getCeva() {
        return ceva;
    }

    public void setCeva(String ceva) {
        this.ceva = ceva;
    }

    public void loadState(PersistencyService state) {
        System.out.println("cstate load");

        XmlSerializerUtil.copyBean(state, this);
    }

    public PersistencyService getState() {
        System.out.println("cstate retu");
        return this;
    }
}
