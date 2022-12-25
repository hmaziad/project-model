package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.SAVED_SNAP_MESSAGE;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class SaveHandler implements ToolHandler {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private final JLabel feedbackLabel;
    private final AtomicInteger counter = new AtomicInteger();
    private final DropdownObserver dropdownObserver;
    private final Project project;

    public void handle(DefaultTreeModel treeModel) {
        DebugNode rootNode = (DebugNode) treeModel.getRoot();
        if (Objects.isNull(rootNode)) {
            this.feedbackLabel.setText("Please take a snap shot first");
            return;
        }
        String nodeName = String.format("Saved-Node-%s", counter.getAndIncrement());
        persistencyService.addNode(nodeName, rootNode);
        this.dropdownObserver.addItem(nodeName);
        this.feedbackLabel.setText(SAVED_SNAP_MESSAGE);
    }

}
