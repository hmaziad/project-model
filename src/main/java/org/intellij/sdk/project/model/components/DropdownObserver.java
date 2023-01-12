package org.intellij.sdk.project.model.components;

import java.util.Objects;

import javax.swing.*;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.ServiceManager;

import lombok.Getter;

@Getter
public class DropdownObserver {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    public static final String CURRENT_DEBUGGER_SESSION = "Current Debugger Session";
    private final JComboBox<String> jComboBox;

    public DropdownObserver(JComboBox<String> jComboBox) {
        this.jComboBox = jComboBox;
        persistencyService.getNodes().keySet().forEach(this::addItem);
    }

    public void addItem(String snapName) {
        this.jComboBox.addItem(snapName);
        this.jComboBox.setSelectedItem(snapName);
    }

    public void addCurrentSession(DebugNode currentSession) {
        if (Objects.nonNull(currentSession)) {
            this.jComboBox.insertItemAt(CURRENT_DEBUGGER_SESSION,0);
        }
    }

    public void removeItem(String item) {
        this.jComboBox.removeItem(item);
    }

    public String getCurrentItem() {
        return (String) this.jComboBox.getSelectedItem();
    }

    public boolean isEmpty() {
        return this.jComboBox.getItemCount() == 0;
    }

}
