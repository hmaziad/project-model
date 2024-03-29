package org.armadillo.core.components.handlers;

import static org.armadillo.core.constants.TextConstants.CURRENT_SESSION;

import java.util.List;
import java.util.Optional;

import javax.swing.*;
import org.armadillo.core.tree.components.DebugNodeContainer;
import com.intellij.openapi.project.Project;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropdownHandler implements ReachServices {
    private int index = 0;
    private final Project project;


    public void addNodesToDropdown(JComboBox<String> nodesDropdown, Optional<Integer> optionalLastIndex) {
        List<String> allNodeNames = nodeHandler.getSortedNodeNames();
        Optional<DebugNodeContainer> currentSession = snapHandler.getCurrentSession(this.project);
        currentSession.ifPresent(node -> nodesDropdown.addItem(CURRENT_SESSION));
        allNodeNames.forEach(nodesDropdown::addItem);
        if (optionalLastIndex.isPresent() && optionalLastIndex.get() < nodesDropdown.getItemCount()) {
            nodesDropdown.setSelectedIndex(optionalLastIndex.get());
        } else if (nodesDropdown.getItemCount() > 0) {
            int remaining = nodesDropdown.getItemCount() - index;
            nodesDropdown.setSelectedIndex(Math.min(this.index++, remaining));
        }
    }

    public DebugNodeContainer getSelectedContainer(JComboBox<String> nodesDropdown) {
        String selectedItem = (String) nodesDropdown.getSelectedItem();
        if (CURRENT_SESSION.equals(selectedItem)) {
            return nodeHandler.getCurrentSession(this.project);
        } else {
            return nodeHandler.getNodeContainerByName(selectedItem).orElseThrow();
        }
    }

    public String getSelectedNodeName(JComboBox<String> nodesDropdown) {
        return (String) nodesDropdown.getSelectedItem();
    }
}
