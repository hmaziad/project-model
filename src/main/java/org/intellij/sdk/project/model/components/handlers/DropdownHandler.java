package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.CURRENT_SESSION;

import java.util.List;
import java.util.Optional;

import javax.swing.*;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import com.intellij.openapi.project.Project;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DropdownHandler {
    private final NodeHandler nodeHandler = new NodeHandler();
    private final SnapHandler snapHandler = new SnapHandler();
    private int index = 0;
    private final Project project;

    public void addNodesToDropdown(JComboBox<String> nodesDropdown) {
        List<String> allNodeNames = this.nodeHandler.getAllNodeNames();
        Optional<DebugNode> currentSession = this.snapHandler.getCurrentSession(this.project);
        currentSession.ifPresent(node -> nodesDropdown.addItem(CURRENT_SESSION));
        allNodeNames.forEach(nodesDropdown::addItem);
        if (nodesDropdown.getItemCount() > 0) {
            int remaining = nodesDropdown.getItemCount() - index;
            nodesDropdown.setSelectedIndex(Math.min(this.index++, remaining));
        }
    }

    public DebugNode getSelectedNode(JComboBox<String> nodesDropdown) {
        String selectedItem = (String) nodesDropdown.getSelectedItem();
        if (CURRENT_SESSION.equals(selectedItem)) {
            return this.snapHandler.getCurrentSession(this.project).orElseThrow(() -> new IllegalStateException("Why you messing?"));
        } else {
            return this.nodeHandler.getNodeByName(selectedItem);
        }
    }

    public String getSelectedNodeName(JComboBox<String> nodesDropdown) {
        return (String) nodesDropdown.getSelectedItem();
    }
}
