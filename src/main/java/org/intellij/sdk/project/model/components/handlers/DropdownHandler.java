package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.CURRENT_SESSION;

import java.util.List;
import java.util.Optional;

import javax.swing.*;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import com.intellij.openapi.project.Project;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropdownHandler implements ReachServices {
    private int index = 0;
    private final Project project;

    public void addNodesToDropdown(JComboBox<String> nodesDropdown, Optional<Integer> optionalLastIndex) {
        List<String> allNodeNames = COMPONENT_SERVICE.getNodeHandler().getSortedNodeNames();
        Optional<DebugNodeContainer> currentSession = COMPONENT_SERVICE.getSnapHandler().getCurrentSession(this.project);
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
            return COMPONENT_SERVICE.getNodeHandler().getCurrentSession(this.project);
        } else {
            return COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedItem).orElseThrow();
        }
    }

    public String getSelectedNodeName(JComboBox<String> nodesDropdown) {
        return (String) nodesDropdown.getSelectedItem();
    }
}
