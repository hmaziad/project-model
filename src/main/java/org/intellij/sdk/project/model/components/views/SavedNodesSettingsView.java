package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.services.ParserService.convertNodeToString;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.components.handlers.DeleteHandler;
import org.intellij.sdk.project.model.components.handlers.SaveHandler;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

public class SavedNodesSettingsView extends DialogWrapper {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private Project project;
    private SaveHandler saveHandler;
    private DeleteHandler deleteHandler;
    private DefaultTreeModel treeModel;
    private JBList<String> keysList;

    public SavedNodesSettingsView(@NotNull Project project, SaveHandler saveHandler, DeleteHandler deleteHandler, DefaultTreeModel treeModel) {
        super(true); // use current window as parent
        this.project = project;
        this.saveHandler = saveHandler;
        this.deleteHandler = deleteHandler;
        this.treeModel = treeModel;
        setTitle("Manage Saved Nodes");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1300, 1100);
        Map<String, DebugNode> nodes = persistencyService.getNodes();
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        JScrollPane scrollableNodesPanel = getScrollableNodesPanel();
        JScrollPane scrollableKeysPanel = getScrollableKeysPanel(nodes, scrollableNodesPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        dialogPanel.add(scrollableKeysPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        dialogPanel.add(getButtonsPanel(), gbc);

        gbc.gridx = 2;
        gbc.weightx = 1;
        dialogPanel.add(scrollableNodesPanel, gbc);
        return dialogPanel;
    }

    private JPanel getButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(50, 5, 0, 5));
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        deleteButton.addActionListener(e -> deleteNodeAndRefreshJList(this.keysList.getSelectedValue(), false));

        panel.add(deleteButton);
        JButton renameButton = new JButton("Rename");
        renameButton.setEnabled(false);
        this.keysList.addListSelectionListener(e -> {
            if (e.getFirstIndex() > -1) {
                deleteButton.setEnabled(true);
                renameButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
                renameButton.setEnabled(false);
            }
        });

        renameButton.addActionListener(e -> {
            String selectedKey = this.keysList.getSelectedValue();
            Map<String, DebugNode> nodes = persistencyService.getNodes();
            DebugNode debugNode = nodes.get(selectedKey);
            String newNodeName = MessageDialogues.getRenameDialogue(this.project,null);
            while (nodes.containsKey(newNodeName)) {
                newNodeName = MessageDialogues.getRenameDialogue(this.project, String.format("\"%s\" already exists", newNodeName));
            }
            if (Objects.nonNull(newNodeName)) {
                this.saveHandler.savedNode(newNodeName, debugNode);
                deleteNodeAndRefreshJList(selectedKey, true);
            }
        });

        panel.add(renameButton);
        return panel;
    }

    private void deleteNodeAndRefreshJList(String selectedNode, boolean withOutDialogue) {
        deleteHandler.handle(treeModel, selectedNode, withOutDialogue);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(persistencyService.getNodes().keySet().stream().sorted().collect(Collectors.toList()));
        this.keysList.setModel(model);
    }

    @NotNull
    private JScrollPane getScrollableKeysPanel(Map<String, DebugNode> nodes, JScrollPane scrollableNodesPanel) {
        JScrollPane scrollableKeysPanel = new JBScrollPane();
//        scrollableKeysPanel.setPreferredSize(new Dimension(350, 1100));
//        scrollableKeysPanel.setSize(new Dimension(350, 1100));
        scrollableKeysPanel.setMinimumSize(new Dimension(350, 1100));
        scrollableKeysPanel.setBorder(BorderFactory.createTitledBorder("Saved Node names"));

        String[] keyStrings = Optional.ofNullable(persistencyService.getNodes()) //
            .orElse(new HashMap<>()) //
            .keySet() //
            .stream() //
            .sorted() //
            .toArray(String[]::new);

        this.keysList = new JBList<>(keyStrings);
        this.keysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.keysList.addListSelectionListener(e -> onKeySelected(nodes, scrollableNodesPanel));

        JPanel keysPanel = new JPanel(new BorderLayout());
        keysPanel.add(this.keysList, BorderLayout.WEST);
        scrollableKeysPanel.setViewportView(keysPanel);

        return scrollableKeysPanel;
    }

    @NotNull
    private JScrollPane getScrollableNodesPanel() {
        JScrollPane scrollableNodesPanel = new JBScrollPane();
        scrollableNodesPanel.setBorder(BorderFactory.createTitledBorder("Node Content"));
        scrollableNodesPanel.setPreferredSize(new Dimension(0, 1100));
        return scrollableNodesPanel;
    }

    private void onKeySelected(Map<String, DebugNode> nodes, JScrollPane scrollableNodesPanel) {
        String selectedValue = this.keysList.getSelectedValue();
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