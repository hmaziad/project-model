package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.SAVED_SNAP_MESSAGE;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.ServiceManager;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Value
public class SaveHandler implements ToolHandler {
    static PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    JLabel feedbackLabel;
    AtomicInteger counter = new AtomicInteger();
    DropdownObserver savedDropdownObserver;
    DropdownObserver refDropdownObserver;

    public void handle(DefaultTreeModel treeModel) {
        DebugNode rootNode = (DebugNode) treeModel.getRoot();
        if (Objects.isNull(rootNode)) {
            this.feedbackLabel.setText("Please take a snap shot first");
            return;
        }
        String nodeName = String.format("Saved-Node-%s", counter.getAndIncrement());
        persistencyService.addNode(nodeName, rootNode);
        this.savedDropdownObserver.addItem(nodeName);
        this.refDropdownObserver.addItem(nodeName);
        this.feedbackLabel.setText(SAVED_SNAP_MESSAGE);
    }

}
