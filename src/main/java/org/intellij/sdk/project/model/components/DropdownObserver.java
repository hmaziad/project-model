package org.intellij.sdk.project.model.components;

import javax.swing.*;
import org.intellij.sdk.project.model.services.PersistencyService;
import com.intellij.openapi.components.ServiceManager;

public class DropdownObserver {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private final JComboBox<String> jComboBox;

    public DropdownObserver(JComboBox<String> jComboBox) {
        this.jComboBox = jComboBox;
        persistencyService.getNodes().keySet().forEach(this::addItem);
    }

    public void addItem(String snapName) {
        this.jComboBox.addItem(snapName);
        this.jComboBox.setSelectedItem(snapName);
    }

    public void removeItem(String item) {
        this.jComboBox.removeItem(item);
    }

    public String getCurrentItem() {
        return (String) this.jComboBox.getSelectedItem();
    }
}
