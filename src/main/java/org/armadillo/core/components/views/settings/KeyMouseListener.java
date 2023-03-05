package org.armadillo.core.components.views.settings;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;

public class KeyMouseListener extends MouseAdapter {
    private final KeyPopup keyPopup;

    public KeyMouseListener(JBList<String> keysList, Project project, JLabel description, Integer lineNumber){
        this.keyPopup = new KeyPopup(keysList, project, description, lineNumber);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            this.keyPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
