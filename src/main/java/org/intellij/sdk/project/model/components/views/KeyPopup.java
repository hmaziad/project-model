package org.intellij.sdk.project.model.components.views;

import javax.swing.*;

public class KeyPopup extends JPopupMenu {
    public KeyPopup() {
        JMenuItem expand = new JMenuItem("Expand");
        JMenuItem collapse = new JMenuItem("Collapse");
        JMenuItem expandAll = new JMenuItem("Expand All");
        JMenuItem collapseAll = new JMenuItem("Collapse All");
        add(expand);
        add(collapse);
        add(new JSeparator());
        add(expandAll);
        add(collapseAll);
    }
}
