package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.constants.TextConstants.SAVED_SNAP_MESSAGE;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.XTestCompositeNode;
import com.intellij.openapi.components.ServiceManager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class SaveHandler implements ToolHandler{
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private final JLabel feedbackLabel;
    private final AtomicInteger counter = new AtomicInteger();

    public void handle(DefaultTreeModel treeModel) {
        XTestCompositeNode rootNode = (XTestCompositeNode) treeModel.getRoot();
        String nodeName = String.format("Saved-Node-%s", counter.getAndIncrement());
        persistencyService.addNode(nodeName, rootNode);
        this.feedbackLabel.setText(SAVED_SNAP_MESSAGE);
    }

}
