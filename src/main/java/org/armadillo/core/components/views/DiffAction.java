package org.armadillo.core.components.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

public class DiffAction implements Action {
    private Runnable doDiff;

    public DiffAction(Runnable doDiff) {
        this.doDiff = doDiff;
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
        this.doDiff.run();
    }
}
