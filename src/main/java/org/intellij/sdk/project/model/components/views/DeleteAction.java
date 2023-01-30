package org.intellij.sdk.project.model.components.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

public class DeleteAction implements Action {
    private Runnable doRun;

    public DeleteAction(Runnable doRun) {
        this.doRun = doRun;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void putValue(String key, Object value) {
        // nothing
    }

    @Override
    public void setEnabled(boolean b) {
        // nothing
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // nothing

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // nothing
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.doRun.run();
    }
}
