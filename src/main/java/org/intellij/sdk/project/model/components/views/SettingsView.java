package org.intellij.sdk.project.model.components.views;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.DebuggerTreeRenderer;
import org.intellij.sdk.project.model.components.handlers.NodeHandler;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class SettingsView extends DialogWrapper implements ReachServices {
    private final Project project;
    private final NodeHandler nodeHandler = new NodeHandler();
    private JScrollPane scrollableNodesPanel;

    public SettingsView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        setTitle("Manage Saved Nodes");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1300, 1100);
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        String[] keyStrings = this.nodeHandler.getAllNodeNames().toArray(String[]::new);
        JBList<String> keysList = new JBList<>(keyStrings);

        JScrollPane scrollableKeysPanel = getScrollableKeysPanel(keysList);
        this.scrollableNodesPanel = getScrollableNodesPanel();

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
        dialogPanel.add(getButtonsPanel(keysList), gbc);
        return dialogPanel;
    }

    private JPanel getButtonsPanel(JBList<String> keysList) {
        JPanel panel = new JPanel();
        panel.setBorder(JBUI.Borders.empty(50, 5, 0, 5));
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        // delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteNode(keysList));
        panel.add(deleteButton);
        // rename button
        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(e -> renameNodeName(keysList));
        panel.add(renameButton);
        // load button
        JButton loadButton = new JButton("Show");
        loadButton.addActionListener(e -> loadNode(keysList));
        panel.add(loadButton);
        // delete all button
        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.addActionListener(e -> deleteAll(keysList));
        panel.add(deleteAllButton);

        refreshView(keysList);

        enableButtons(keysList.getItemsCount() != 0, deleteButton, renameButton, loadButton, deleteAllButton);
        keysList.addListSelectionListener(e -> enableButtons(keysList.getItemsCount() != 0, deleteButton, renameButton, loadButton, deleteAllButton));
        return panel;
    }

    private void loadNode(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        DebugNode debugNode = this.nodeHandler.getNodeByName(selectedKey);
        ((DefaultTreeModel) COMPONENT_SERVICE.getDebugTree().getModel()).setRoot(debugNode);
    }

    private void deleteAll(JBList<String> keysList) {
        this.nodeHandler.deleteAll(this.project);
        refreshView(keysList);
    }

    private void enableButtons(boolean enable, JButton deleteButton, JButton renameButton, JButton loadButton, JButton deleteAllButton) {
        deleteButton.setEnabled(enable);
        renameButton.setEnabled(enable);
        loadButton.setEnabled(enable);
        deleteAllButton.setEnabled(enable);
    }

    private void renameNodeName(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedNodeName, false);
        while (this.nodeHandler.getAllNodesPerNames().containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            this.nodeHandler.renameNode(selectedNodeName, newNodeName);
            refreshView(keysList);
        }
    }

    private void deleteNode(JBList<String> keysList) {
        this.nodeHandler.delete(keysList.getSelectedValue(), this.project);
        refreshView(keysList);
    }

    private void refreshView(JBList<String> keysList) {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(this.nodeHandler.getAllNodeNames());
        keysList.setModel(model);
        int currentIndex = Math.min(keysList.getItemsCount(), 0);
        keysList.setSelectedIndex(currentIndex);
        showSelectedNodeContent(keysList);
    }

    @NotNull
    private JScrollPane getScrollableKeysPanel(JBList<String> keysList) {
        JScrollPane scrollableKeysPanel = new JBScrollPane();
        scrollableKeysPanel.setMinimumSize(new Dimension(350, 1100));
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Saved Sessions");
        titledBorder.setBorder(new LineBorder(Color.darkGray.darker()));
        scrollableKeysPanel.setBorder(titledBorder);

        keysList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keysList.addListSelectionListener(e -> showSelectedNodeContent(keysList));

        JPanel keysPanel = new JPanel(new BorderLayout());
        keysPanel.add(keysList, BorderLayout.WEST);
        scrollableKeysPanel.setViewportView(keysPanel);

        return scrollableKeysPanel;
    }

    @NotNull
    private JScrollPane getScrollableNodesPanel() {
        JScrollPane jScrollPane = new JBScrollPane();
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Session Content");
        titledBorder.setBorder(new LineBorder(Color.darkGray.darker()));
        jScrollPane.setBorder(titledBorder);
        jScrollPane.setPreferredSize(new Dimension(0, 1100));
        return jScrollPane;
    }

    private void showSelectedNodeContent(JBList<String> keysList) {
        String selectedValue = keysList.getSelectedValue();
        DebugNode debugNode = this.nodeHandler.getNodeByName(selectedValue);

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