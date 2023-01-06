package org.intellij.sdk.project.model.components;

import static org.intellij.sdk.project.model.services.ParserService.convertNodeToString;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

public class SavedNodesView extends DialogWrapper {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);

    public SavedNodesView() {
        super(true); // use current window as parent
        setTitle("Manage Saved Nodes");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1300, 1100);
        Map<String, DebugNode> nodes = persistencyService.getNodes();
        JPanel dialogPanel = new JPanel(new GridLayout());

        JPanel keysPanel = getKeysPanel();
        JList<String> keysList = new JBList<>(nodes.keySet().toArray(new String[0]));
        keysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollableKeysPanel = new JBScrollPane();
        scrollableKeysPanel.setMaximumSize(new Dimension(300, 1100));
        scrollableKeysPanel.setBorder(BorderFactory.createTitledBorder("Saved Node names"));
        scrollableKeysPanel.setViewportView(keysList);


        JScrollPane scrollableNodesPanel = new JBScrollPane();
        scrollableNodesPanel.setBorder(BorderFactory.createTitledBorder("Node Content"));

        keysPanel.add(keysList);
        keysList.addListSelectionListener(e -> onKeySelected(nodes, scrollableNodesPanel, keysList));

        dialogPanel.add(scrollableKeysPanel);
        dialogPanel.add(scrollableNodesPanel);
        return dialogPanel;
    }

    @NotNull
    private JPanel getKeysPanel() {
        return new JPanel(new FlowLayout());
    }

    private void onKeySelected(Map<String, DebugNode> nodes, JScrollPane scrollableNodesPanel, JList<String> keysList) {
        String selectedValue = keysList.getSelectedValue();
        DebugNode debugNode = nodes.get(selectedValue);
        String text = "Node does not contain any text...";
        if (Objects.nonNull(debugNode)) {
            text = convertNodeToString(debugNode);
        }
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(text);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea);
        scrollableNodesPanel.setViewportView(panel);
    }

}