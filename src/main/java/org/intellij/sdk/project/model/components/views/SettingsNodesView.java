package org.intellij.sdk.project.model.components.views;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.DebuggerTreeRenderer;
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
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class SettingsNodesView extends DialogWrapper {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private Project project;
    private SaveHandler saveHandler;
    private DeleteHandler deleteHandler;
    private DefaultTreeModel treeModel;
    private JBList<String> keysList;
    private JScrollPane scrollableNodesPanel;
    private JScrollPane scrollableKeysPanel;

    public SettingsNodesView(@NotNull Project project, SaveHandler saveHandler, DeleteHandler deleteHandler, DefaultTreeModel treeModel) {
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
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        this.scrollableNodesPanel = getScrollableNodesPanel();
        this.scrollableKeysPanel = getScrollableKeysPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        dialogPanel.add(scrollableKeysPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        dialogPanel.add(scrollableNodesPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        dialogPanel.add(getButtonsPanel(), gbc);
        return dialogPanel;
    }

    private Map<String, DebugNode> getNodes() {
        return persistencyService.getNodes();
    }

    private JPanel getButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(JBUI.Borders.empty(50, 5, 0, 5));
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        // delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteNodeAndRefreshJList(this.keysList.getSelectedValue(), false));
        panel.add(deleteButton);
        // rename button
        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(e -> renameNodeName());
        panel.add(renameButton);
        // load button
        JButton loadButton = new JButton("Show");
        loadButton.addActionListener(e -> loadNode());
        panel.add(loadButton);
        // delete all button
        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.addActionListener(e -> deleteAll());
        panel.add(deleteAllButton);

        enableButtons(this.keysList.getItemsCount() != 0, deleteButton, renameButton, loadButton, deleteAllButton);
        this.keysList.addListSelectionListener(e -> enableButtons(this.keysList.getItemsCount() != 0, deleteButton, renameButton, deleteAllButton, deleteAllButton));
        return panel;
    }

    private void loadNode() {
        String selectedKey = this.keysList.getSelectedValue();
        Map<String, DebugNode> nodes = getNodes();
        DebugNode debugNode = nodes.get(selectedKey);
        this.treeModel.setRoot(debugNode);
    }

    private void deleteAll() {
        boolean userAgrees = MessageDialogues.getYesNoMessageDialogue("Are you sure you want to delete all the node", "Delete All Saved Sessions", this.project);
        if (userAgrees) {
            persistencyService.getNodes().clear();
            this.keysList.setModel(new DefaultListModel<>());
        }
    }

    private void enableButtons(boolean enable, JButton deleteButton, JButton renameButton, JButton loadButton, JButton deleteAllButton) {
        deleteButton.setEnabled(enable);
        renameButton.setEnabled(enable);
        loadButton.setEnabled(enable);
        deleteAllButton.setEnabled(enable);
    }

    private void renameNodeName() {
        String selectedKey = this.keysList.getSelectedValue();
        Map<String, DebugNode> nodes = getNodes();
        DebugNode debugNode = nodes.get(selectedKey);
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedKey, false);
        while (nodes.containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            this.saveHandler.savedNode(newNodeName, debugNode);
            deleteNodeAndRefreshJList(selectedKey, true);
        }
    }

    private void deleteNodeAndRefreshJList(String selectedNode, boolean withOutDialogue) {
        deleteHandler.handle(treeModel, selectedNode, withOutDialogue);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(getNodes().keySet().stream().sorted().collect(Collectors.toList()));
        this.keysList.setModel(model);
        nextSelection();
    }

    private void nextSelection() {
        this.keysList.setSelectedIndex(this.keysList.getItemsCount() > -1 ? 0 : -1);
        showSelectedNodeContent();
    }

    @NotNull
    private JScrollPane getScrollableKeysPanel() {
        JScrollPane scrollableKeysPanel = new JBScrollPane();
        scrollableKeysPanel.setMinimumSize(new Dimension(350, 1100));
        scrollableKeysPanel.setBorder(BorderFactory.createTitledBorder("Saved Node names"));

        String[] keyStrings = Optional.ofNullable(getNodes()) //
            .orElse(new HashMap<>()) //
            .keySet() //
            .stream() //
            .sorted() //
            .toArray(String[]::new);

        this.keysList = new JBList<>(keyStrings);
        this.keysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int currentIndex = this.keysList.getItemsCount() > -1 ? 0 : -1;
        this.keysList.setSelectedIndex(currentIndex);
        showSelectedNodeContent();
        this.keysList.addListSelectionListener(e -> showSelectedNodeContent());

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

    private void showSelectedNodeContent() {
        String selectedValue = this.keysList.getSelectedValue();
        DebugNode debugNode = null;
        if (Objects.nonNull(selectedValue)) {
            debugNode = getNodes().get(selectedValue);
        }

        JTree debugTree = new Tree();
        debugTree.setRootVisible(false);
        debugTree.setCellRenderer(new DebuggerTreeRenderer());
        DefaultTreeModel localTreeModel = (DefaultTreeModel) debugTree.getModel();
        localTreeModel.setRoot(debugNode);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(debugTree);
        this.scrollableNodesPanel.setViewportView(panel);
    }

}