package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.SAVED_SNAP_MESSAGE;

import java.util.Objects;
import java.util.UUID;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.text.StringUtil;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Value
public class SaveHandler implements ToolHandler {
    static PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    JLabel feedbackLabel;

    public void handle(DefaultTreeModel treeModel) {
        DebugNode rootNode = (DebugNode) treeModel.getRoot();
        if (Objects.isNull(rootNode)) {
            this.feedbackLabel.setText("Please take a snap shot first");
            return;
        }
        String nodeName = String.format("node-%s", UUID.randomUUID());
        savedNode(nodeName, rootNode);
        this.feedbackLabel.setText(SAVED_SNAP_MESSAGE);
    }

    public void savedNode(String nodeName, DebugNode rootNode) {
        if (StringUtil.isEmpty(nodeName)) {
            throw new IllegalArgumentException("Node name is empty or null " + nodeName);
        }
        persistencyService.addNode(nodeName, rootNode);
    }

}
