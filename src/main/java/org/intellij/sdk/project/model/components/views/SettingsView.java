package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.EXPORT_SESSION_JSON;
import static org.intellij.sdk.project.model.constants.TextConstants.IMPORT_FROM_FILE;
import static org.intellij.sdk.project.model.constants.TextConstants.LOAD_SESSION_INTO_TOOLBAR;
import static org.intellij.sdk.project.model.constants.TextConstants.REMOVE_ALL_SESSIONS;
import static org.intellij.sdk.project.model.constants.TextConstants.REMOVE_SESSION_FROM_STORAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.RENAME_SESSION;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

public class SettingsView extends DialogWrapper implements ReachServices {
    private final Project project;
    private JScrollPane scrollableNodesPanel;
    private final DebugTreeManager debugTreeManager = new DebugTreeManager();

    public SettingsView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        setTitle("Manage Saved Sessions");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1300, 1100);
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        String[] keyStrings = COMPONENT_SERVICE.getNodeHandler().getAllNodeNames().toArray(String[]::new);
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
        deleteButton.setToolTipText(REMOVE_SESSION_FROM_STORAGE);
        deleteButton.addActionListener(e -> deleteNode(keysList));
        panel.add(deleteButton);
        // rename button
        JButton renameButton = new JButton("Rename");
        renameButton.setToolTipText(RENAME_SESSION);
        renameButton.addActionListener(e -> renameNodeName(keysList));
        panel.add(renameButton);
        // load button
        JButton loadButton = new JButton("Load");
        loadButton.setToolTipText(LOAD_SESSION_INTO_TOOLBAR);
        loadButton.addActionListener(e -> loadNode(keysList));
        panel.add(loadButton);
        // delete all button
        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.setToolTipText(REMOVE_ALL_SESSIONS);
        deleteAllButton.addActionListener(e -> deleteAll(keysList));
        panel.add(deleteAllButton);
        // export button
        JButton exportButton = new JButton("Export");
        exportButton.setToolTipText(EXPORT_SESSION_JSON);
        exportButton.addActionListener(e -> export(keysList));
        panel.add(exportButton);
        refreshView(keysList);
        // import button
        JButton importButton = new JButton("Import");
        importButton.setToolTipText(IMPORT_FROM_FILE);
        importButton.addActionListener(e -> _import(keysList));
        panel.add(importButton);
        refreshView(keysList);
        enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, renameButton, loadButton, deleteAllButton);
        keysList.addListSelectionListener(e -> enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, renameButton, loadButton, deleteAllButton));
        return panel;
    }

    private void _import(JBList<String> keysList) {
        COMPONENT_SERVICE.getNodeHandler().doImport(this.project);
        refreshView(keysList);
    }

    private void export(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        COMPONENT_SERVICE.getNodeHandler().export(selectedKey, this.project);
    }

    private void loadNode(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        DebugNode debugNode = COMPONENT_SERVICE.getNodeHandler().getNodeByName(selectedKey);
        COMPONENT_SERVICE.setNodeNameInWindow(selectedKey);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(debugNode);
    }

    private void deleteAll(JBList<String> keysList) {
        COMPONENT_SERVICE.getNodeHandler().deleteAll(this.project);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
        COMPONENT_SERVICE.setNodeNameInWindow(null);
        refreshView(keysList);
    }

    private void enableButtons(boolean enable,JButton ...jButtons) {
        Arrays.stream(jButtons).forEach(jButton -> jButton.setEnabled(enable));
    }

    private void renameNodeName(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedNodeName, false);
        while (COMPONENT_SERVICE.getNodeHandler().getAllNodesPerNames().containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            newNodeName = newNodeName.replace(' ', '_');
            COMPONENT_SERVICE.getNodeHandler().renameNode(selectedNodeName, newNodeName);
            refreshView(keysList);
        }
    }

    private void deleteNode(JBList<String> keysList) {
        COMPONENT_SERVICE.getNodeHandler().delete(keysList.getSelectedValue(), this.project);
        refreshView(keysList);
    }

    private void refreshView(JBList<String> keysList) {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(COMPONENT_SERVICE.getNodeHandler().getAllNodeNames());
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
        titledBorder.setBorder(new LineBorder(Color.gray.darker()));
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
        titledBorder.setBorder(new LineBorder(Color.gray.darker()));
        jScrollPane.setBorder(titledBorder);
        jScrollPane.setPreferredSize(new Dimension(0, 1100));
        return jScrollPane;
    }

    private void showSelectedNodeContent(JBList<String> keysList) {
        String selectedValue = keysList.getSelectedValue();
        DebugNode debugNode = COMPONENT_SERVICE.getNodeHandler().getNodeByName(selectedValue);
        this.debugTreeManager.setRoot(debugNode);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.debugTreeManager.getDebugTree());
        this.scrollableNodesPanel.setViewportView(panel);
    }

}