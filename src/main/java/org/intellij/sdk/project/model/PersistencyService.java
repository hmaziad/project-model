package org.intellij.sdk.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
    name = "Test", storages = {
    @Storage("testpersist.xml")
})
public class PersistencyService implements PersistentStateComponent<PersistencyService> {

    Map<String, XTestCompositeNode> nodes;
    private final List<JComboBox<String>> observers = new ArrayList<>();

    public PersistencyService() {
        nodes = new HashMap<>();
        System.out.println("constr");
    }

    public void addNode(String name, XTestCompositeNode node) {
//        observers.forEach(observer -> observer.addItem(name)); // this line freezes computing node service
        this.nodes.put(name, node);
    }

    public Map<String, XTestCompositeNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, XTestCompositeNode> nodes) {
        this.nodes = nodes;
    }

    public void loadState(PersistencyService state) {
        System.out.println("cstate load: " + state.getNodes().size());
        XmlSerializerUtil.copyBean(state, this);
    }

    public PersistencyService getState() {
        System.out.println("cstate retu "+ this.nodes.size());
        return this;
    }

    public void addObserver(JComboBox<String> targetFilesBox) {
        observers.add(targetFilesBox);
    }
}
