package org.intellij.sdk.project.model;

import java.util.HashMap;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
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

    Map<String, XTestCompositeNode> nodes;

    public PersistencyService() {
        nodes = new HashMap<>();
        LOG.debug("Persistency Service Constructed");
    }

    public void addNode(String name, XTestCompositeNode node) {
        this.nodes.put(name, node);
    }

    public Map<String, XTestCompositeNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, XTestCompositeNode> nodes) {
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
