package org.intellij.sdk.project.model;

import java.util.Map;

import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Storage("yourName.xml")
@Data
public class PersistencyService implements PersistentStateComponent<PersistencyService> {
    public Map<String, XTestCompositeNode> nodes;

    private PersistencyService(){}

    private static class SingletonHelper {
        private static final PersistencyService INSTANCE = new PersistencyService();
    }
    public static PersistencyService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public PersistencyService getState() {
        LOG.debug("Persistency State returned {}", this);
        return this;
    }

    @Override
    public void loadState(PersistencyService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
