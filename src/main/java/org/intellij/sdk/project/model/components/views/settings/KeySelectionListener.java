package org.intellij.sdk.project.model.components.views.settings;

import static org.intellij.sdk.project.model.constants.TextConstants.EMPTY_STRING;
import static org.intellij.sdk.project.model.constants.TextConstants.HUMAN_DATE_FORMAT;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.collections.CollectionUtils;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugFrame;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;

import lombok.Value;

@Value
public class KeySelectionListener implements ListSelectionListener, ReachServices {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(HUMAN_DATE_FORMAT);
    JBList<String> keysList;
    JLabel description;
    JLabel timestamp;
    DebugTreeManager debugTreeManager;
    JBList<DebugFrame> framesList;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        String selectedNodeName = this.keysList.getSelectedValue();
        Optional<DebugNodeContainer> optionalNode = nodeHandler.getNodeContainerByName(selectedNodeName);
        if (optionalNode.isPresent()) {
            DebugNodeContainer nodeContainer = optionalNode.get();
            updateViewNodeData(nodeContainer.getTimestamp(), nodeContainer.getDescription(), nodeContainer.getNode(), nodeContainer.getFrames());
        } else {
            updateViewNodeData(null, EMPTY_STRING, null, null);
        }
    }

    private void updateViewNodeData(LocalDateTime timestamp, String description, DebugNode node, List<DebugFrame> frames) {
        this.timestamp.setText(Objects.isNull(timestamp) ? EMPTY_STRING : timestamp.format(DATE_TIME_FORMATTER));
        String wrappedDescription = String.format("<html><xmp>%s</xmp></html>", description);
        this.description.setText(StringUtil.isEmpty(description) ? EMPTY_STRING : wrappedDescription);
        this.debugTreeManager.setRoot(node);
        DefaultListModel<DebugFrame> framesListModel = new DefaultListModel<>();
        if (CollectionUtils.isNotEmpty(frames)) {
            framesListModel.addAll(frames);
        }
        this.framesList.setModel(framesListModel);
    }

}
