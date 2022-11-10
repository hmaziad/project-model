package org.intellij.sdk.project.model.components;

import javax.swing.*;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DropdownObserver {
    private final JComboBox<String> jComboBox;

    public void addItem(String snapName) {
        this.jComboBox.addItem(snapName);
        this.jComboBox.setSelectedItem(snapName);
    }

    public void removeCurrentItem() {
        this.jComboBox.removeItem(this.jComboBox.getSelectedItem());
    }

    public String getCurrentItem() {
        return (String) this.jComboBox.getSelectedItem();
    }
}
