package org.intellij.sdk.project.model;

import java.util.HashMap;
import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.PersistentStateComponent;

public class PersistencyService implements PersistentStateComponent<PersistencyService.State> {
    private State state = new State();

    private PersistencyService(){}

    private static class SingletonHelper {
        private static final PersistencyService INSTANCE = new PersistencyService();
    }
    public static PersistencyService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public @Nullable PersistencyService.State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    static class State {
        public Map<String, XTestCompositeNode> nodes = new HashMap<>();;
    }

}
