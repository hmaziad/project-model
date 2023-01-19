package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODE;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODES;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.project.Project;

public class NodeHandler implements ReachServices {
    public void save(DebugNode debugNode) {
        String dateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-SSS"));
        String nodeName = String.format("node-%s", dateTimeNow);
        PERSISTENCY_SERVICE.getNodes().put(nodeName, debugNode);
    }

    public void delete(String nodeName, Project project) {
        String message = String.format(DELETE_SAVED_NODE, nodeName);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, "Delete Session", project);
        if (isSure) {
            PERSISTENCY_SERVICE.getNodes().remove(nodeName);
        }
    }

    public void deleteAll(Project project) {
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(DELETE_SAVED_NODES, "Delete Session", project);
        if (isSure) {
            PERSISTENCY_SERVICE.getNodes().clear();
        }
    }

    public DebugNode getNodeByName(String nodeName) {
        return PERSISTENCY_SERVICE.getNodes().get(nodeName);
    }

    public List<DebugNode> getAllNodes() {
        return new ArrayList<>(PERSISTENCY_SERVICE //
            .getNodes() //
            .values());
    }

    public List<String> getAllNodeNames() {
        return PERSISTENCY_SERVICE //
            .getNodes() //
            .keySet() //
            .stream() //
            .sorted(Comparator.reverseOrder()) //
            .collect(Collectors.toList());
    }

    public Map<String,DebugNode> getAllNodesPerNames() {
        return PERSISTENCY_SERVICE.getNodes();
    }

    public void renameNode(String from, String to) {
        Map<String, DebugNode> nodes = PERSISTENCY_SERVICE.getNodes();
        DebugNode fromNode = nodes.get(from);
        nodes.remove(from);
        nodes.put(to, fromNode);
    }

}
