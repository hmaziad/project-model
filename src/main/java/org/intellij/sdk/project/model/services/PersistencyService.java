package org.intellij.sdk.project.model.services;

import java.util.HashMap;
import java.util.Map;

import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.intellij.sdk.project.model.util.DebugContainerConverter;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;

import lombok.extern.log4j.Log4j2;

@State(
    name = "PersistencyServiceState", storages = {
    @Storage("PersistencyServiceStorage.xml")
})
@Log4j2
public class PersistencyService implements PersistentStateComponent<PersistencyService> {

    @OptionTag(converter = DebugContainerConverter.class)
    Map<String, DebugNodeContainer> containers;

    public PersistencyService() {
        containers = new HashMap<>();
        LOG.debug("Persistency Service Constructed");
    }

    public void addContainer(String name, DebugNodeContainer node) {
        this.containers.put(name, node);
    }

    public Map<String, DebugNodeContainer> getContainers() {
        return containers;
    }

    public void setContainers(Map<String, DebugNodeContainer> containers) {
        this.containers = containers;
    }

    public void loadState(PersistencyService state) {
        LOG.debug("Load State called, {}", state.getContainers());
        XmlSerializerUtil.copyBean(state, this);
    }

    public PersistencyService getState() {
        LOG.debug("Get State called nodes {}", this.containers);
        return this;
    }

}
