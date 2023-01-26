package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.EDIT_SESSION;
import static org.intellij.sdk.project.model.constants.TextConstants.EXPORT_SESSION_JSON;
import static org.intellij.sdk.project.model.constants.TextConstants.HUMAN_DATE_FORMAT;
import static org.intellij.sdk.project.model.constants.TextConstants.IMPORT_FROM_FILE;
import static org.intellij.sdk.project.model.constants.TextConstants.LOAD_SESSION_INTO_TOOLBAR;
import static org.intellij.sdk.project.model.constants.TextConstants.REMOVE_ALL_SESSIONS;
import static org.intellij.sdk.project.model.constants.TextConstants.REMOVE_SESSION_FROM_STORAGE;
import static org.intellij.sdk.project.model.constants.TextConstants.RENAME_SESSION;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
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
    private final DebugTreeManager debugTreeManager = new DebugTreeManager(false);

    public SettingsView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        setTitle("Manage Saved Sessions");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1100, 800);
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        String[] keyStrings = COMPONENT_SERVICE.getNodeHandler().getAllNodeNames().toArray(String[]::new);
        JBList<String> keysList = new JBList<>(keyStrings);

        JScrollPane scrollableKeysPanel = getScrollableKeysPanel(keysList);
        this.scrollableNodesPanel = getScrollableNodesPanel();

        JLabel timestampLabel = new JLabel();
        JLabel descriptionLabel = new JLabel();
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 0;

        EmptyBorder emptyBorder1 = new EmptyBorder(0, 2, 4, 0);
        timestampLabel.setBorder(emptyBorder1);
        dialogPanel.add(timestampLabel, gbc);

        gbc.gridy = 1;
        EmptyBorder emptyBorder2 = new EmptyBorder(0, 2, 20, 0);
        descriptionLabel.setBorder(emptyBorder2);
        dialogPanel.add(descriptionLabel, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 2;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 1;
        dialogPanel.add(scrollableKeysPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 5;
        dialogPanel.add(scrollableNodesPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        dialogPanel.add(getButtonsPanel(keysList), gbc);

        updateMetaData(keysList, timestampLabel, descriptionLabel);
        keysList.addListSelectionListener(e -> updateMetaData(keysList, timestampLabel, descriptionLabel));
        return dialogPanel;
    }

    private void updateMetaData(JBList<String> keysList, JLabel timestampLabel, JLabel descriptionLabel) {
        String selectedValue = keysList.getSelectedValue();
        Optional<DebugNodeContainer> optionalContainer = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedValue);
        if (optionalContainer.isPresent()) {
            DebugNodeContainer container = optionalContainer.get();
            if (Objects.nonNull(container.getTimestamp())) {
                String formattedTimestamp = container.getTimestamp().format(DateTimeFormatter.ofPattern(HUMAN_DATE_FORMAT));
                timestampLabel.setText("Created on: " + formattedTimestamp);
            } else {
                timestampLabel.setText("");
            }
            if (Objects.nonNull(container.getDescription())) {
                descriptionLabel.setText("Description: " + container.getDescription());
            } else {
                descriptionLabel.setText("");
            }
        }
    }

    private JPanel getButtonsPanel(JBList<String> keysList) {
        JPanel panel = new JPanel();
        panel.setBorder(JBUI.Borders.empty(50, 5, 0, 5));
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.setLayout(boxLayout);
        // rename button
        JButton renameButton = new JButton("Rename");
        renameButton.setToolTipText(RENAME_SESSION);
        renameButton.addActionListener(e -> renameButton(keysList));
        panel.add(renameButton);
        // edit button
        JButton editButton = new JButton("Describe");
        editButton.setToolTipText(EDIT_SESSION);
        editButton.addActionListener(e -> editButton(keysList));
        panel.add(editButton);

        // delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setToolTipText(REMOVE_SESSION_FROM_STORAGE);
        deleteButton.addActionListener(e -> deleteNode(keysList));
        panel.add(deleteButton);
        // load button
        JButton loadButton = new JButton("Load");
        loadButton.setToolTipText(LOAD_SESSION_INTO_TOOLBAR);
        loadButton.addActionListener(e -> loadNode(keysList));
        panel.add(loadButton);

        // export button
        JButton exportButton = new JButton("Export");
        exportButton.setToolTipText(EXPORT_SESSION_JSON);
        exportButton.addActionListener(e -> export(keysList));
        panel.add(exportButton);
        // import button
        JButton importButton = new JButton("Import");
        importButton.setToolTipText(IMPORT_FROM_FILE);
        importButton.addActionListener(e -> _import(keysList));
        panel.add(importButton);
        // delete all button
        JButton deleteAllButton = new JButton("Delete All");
        deleteAllButton.setToolTipText(REMOVE_ALL_SESSIONS);
        deleteAllButton.addActionListener(e -> deleteAll(keysList));
        panel.add(deleteAllButton);
        refreshView(keysList);
        enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, editButton, loadButton, deleteAllButton, renameButton);
        keysList.addListSelectionListener(e -> enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, editButton, loadButton, deleteAllButton));
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
        DebugNodeContainer nodeContainer = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedKey).orElseThrow();
        COMPONENT_SERVICE.setNodeNameInWindow(selectedKey);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(nodeContainer.getNode());
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

    private void renameButton(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedNodeName, false);
        while (COMPONENT_SERVICE.getNodeHandler().getAllContainersPerNames().containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            newNodeName = newNodeName.replace(' ', '_');
            COMPONENT_SERVICE.getNodeHandler().renameNode(selectedNodeName, newNodeName);
            refreshView(keysList);
        }
    }

    private void editButton(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        Optional<DebugNodeContainer> container = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedNodeName);
        String currentDescription = null;
        if (container.isPresent()) {
            currentDescription = container.get().getDescription();
        }
        String description = MessageDialogues.getEditDialogue(this.project, selectedNodeName, currentDescription);
        if (Objects.nonNull(description)) {
            container.ifPresent(debugNodeContainer -> debugNodeContainer.setDescription(description));
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
        scrollableKeysPanel.setPreferredSize(new Dimension(200, 800));
        scrollableKeysPanel.setMinimumSize(new Dimension(200, 800));
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
        jScrollPane.setPreferredSize(new Dimension(450, 800));
        jScrollPane.setMinimumSize(new Dimension(450, 800));
        return jScrollPane;
    }


    private void showSelectedNodeContent(JBList<String> keysList) {
        String selectedValue = keysList.getSelectedValue();
        Optional<DebugNodeContainer> optionalContainer = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedValue);
        this.debugTreeManager.setRoot(optionalContainer.isPresent() ? optionalContainer.get().getNode() : null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this.debugTreeManager.getDebugTree());
        this.scrollableNodesPanel.setViewportView(panel);
    }

}