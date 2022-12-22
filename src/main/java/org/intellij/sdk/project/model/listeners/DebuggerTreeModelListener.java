package org.intellij.sdk.project.model.listeners;

import static org.intellij.sdk.project.model.components.ButtonType.CLEAR;
import static org.intellij.sdk.project.model.components.ButtonType.SAVE;
import static org.intellij.sdk.project.model.components.ButtonType.SNAP;
import static org.intellij.sdk.project.model.constants.TextConstants.DEBUGGER_SNAP_TAKEN;
import static org.intellij.sdk.project.model.constants.TextConstants.TAKE_DEBUGGER_SNAP;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.ButtonType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DebuggerTreeModelListener implements TreeModelListener {
    private final JLabel feedbackLabel;
    private Map<ButtonType, JButton> allButtons;

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        //nothing to change
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        boolean isRootNull = Objects.isNull(((DefaultTreeModel) e.getSource()).getRoot());
        this.allButtons = new EnumMap<>(ButtonType.class); // todo, removed when session listener is implemented
        if (isRootNull) {
            this.feedbackLabel.setText(TAKE_DEBUGGER_SNAP);
            this.allButtons.values().forEach(button -> button.setEnabled(false));
        } else {
            this.feedbackLabel.setText(DEBUGGER_SNAP_TAKEN);
            Set<ButtonType> enabledOnNewNode = Set.of(SNAP, CLEAR, SAVE);
            this.allButtons
                .entrySet()
                .stream()
                .filter(entry -> enabledOnNewNode.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .forEach(button -> button.setEnabled(true));
        }
    }
}
