package org.intellij.sdk.project.model;

import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
//@Storage("yourName.xml")xml
@Storage(StoragePathMacros.WORKSPACE_FILE)
@Data
public class PersistencyService implements PersistentStateComponent<PersistencyService.State> {

    private static PersistencyService service = new PersistencyService();

    public static PersistencyService getInstance() {
        return service;
    }

    static class State {
        public Map<String, XTestCompositeNode> stateNodes;

        @Override
        public String toString() {
            return "State{" + "stateNodes=" + stateNodes + '}';
        }
    }

    private State myState = new State();

    public State getState() {
        LOG.debug("Calling getState...");
        return myState;
    }

    public void loadState(State state) {
        LOG.debug("Loading state {}", state);
        myState = state;
    }

}
