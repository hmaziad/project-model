package org.intellij.sdk.project.model.services;

import java.util.HashMap;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import lombok.extern.log4j.Log4j2;

@State(
    name = "PersistencyServiceState", storages = {
    @Storage("PersistencyServiceStorage.xml")
})
@Log4j2
public class PersistencyService implements PersistentStateComponent<PersistencyService> {

    Map<String, DebugNode> nodes;

    public PersistencyService() {
        nodes = new HashMap<>();
        LOG.debug("Persistency Service Constructed");
    }

    public void addNode(String name, DebugNode node) {
        this.nodes.put(name, node);
    }

    public Map<String, DebugNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, DebugNode> nodes) {
        this.nodes = nodes;
    }

    public void loadState(PersistencyService state) {
        LOG.debug("Load State called");
        XmlSerializerUtil.copyBean(state, this);
    }

    public PersistencyService getState() {
        LOG.debug("Get State called with {} nodes", this.nodes.size());
        return this;
    }

}
