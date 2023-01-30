package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.HUMAN_DATE_FORMAT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

public class SettingsView extends DialogWrapper implements ReachServices {
    private final Project project;
    private JScrollPane scrollableNodesPanel;
    private final DebugTreeManager debugTreeManager = new DebugTreeManager(false);
    private JBList<String> keysList;

    public SettingsView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        setTitle("Armadillo: Manage Saved Sessions");
        init();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.keysList;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setSize(1100, 800);
        JPanel dialogPanel = new JPanel(new GridBagLayout());

        String[] keyStrings = COMPONENT_SERVICE.getNodeHandler().getAllNodeNames().toArray(String[]::new);
        JBList<String> keysList = new JBList<>(keyStrings);
        this.keysList = keysList;
        KeyPopup keyPopup = new KeyPopup(this.keysList, this.project, resetIndex -> refreshView(this.keysList, resetIndex));
        keysList.addMouseListener(getMouseAdapter(keyPopup));

        InputMap inputMap = this.keysList.getInputMap(JComponent.WHEN_FOCUSED);
        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "press");
        this.keysList.getActionMap().put("press", new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {
                // nothing
            }

            @Override
            public void setEnabled(boolean b) {
                // nothing
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {
                // nothing

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {
                // nothing
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Diff sessions");
            }
        });

        new ListSpeedSearch<>(keysList);
        JScrollPane scrollableKeysPanel = getScrollableKeysPanel(keysList);
        this.scrollableNodesPanel = getScrollableNodesPanel();

        JLabel timestampLabel = new JLabel();
        JLabel descriptionLabel = new JLabel("Description: ");
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
                descriptionLabel.setText("<html>Description: " + container.getDescription() + "</html>");
            } else {
                descriptionLabel.setText("Description: ");
            }
        }
    }

    //    private JPanel getButtonsPanel(JBList<String> keysList) {
    //        JPanel panel = new JPanel();
    //        panel.setBorder(JBUI.Borders.empty(50, 5, 0, 5));
    //        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
    //        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    //        panel.setLayout(boxLayout);
    //        // rename button
    //        JButton renameButton = new JButton("Rename");
    //        renameButton.setToolTipText(RENAME_SESSION);
    //        renameButton.addActionListener(e -> renameButton(keysList));
    //        panel.add(renameButton);
    //        // edit button
    //        JButton editButton = new JButton("Describe");
    //        editButton.setToolTipText(EDIT_SESSION);
    //        editButton.addActionListener(e -> editButton(keysList));
    //        panel.add(editButton);
    //
    //        // delete button
    //        JButton deleteButton = new JButton("Delete");
    //        deleteButton.setToolTipText(REMOVE_SESSION_FROM_STORAGE);
    //        deleteButton.addActionListener(e -> deleteNode(keysList));
    //        panel.add(deleteButton);
    //        // load button
    //        JButton loadButton = new JButton("Load");
    //        loadButton.setToolTipText(LOAD_SESSION_INTO_TOOLBAR);
    //        loadButton.addActionListener(e -> loadNode(keysList));
    //        panel.add(loadButton);
    //
    //        // export button
    //        JButton exportButton = new JButton("Export");
    //        exportButton.setToolTipText(EXPORT_SESSION_JSON);
    //        exportButton.addActionListener(e -> export(keysList));
    //        panel.add(exportButton);
    //        // import button
    //        JButton importButton = new JButton("Import");
    //        importButton.setToolTipText(IMPORT_FROM_FILE);
    //        importButton.addActionListener(e -> _import(keysList));
    //        panel.add(importButton);
    //        // delete all button
    //        JButton deleteAllButton = new JButton("Delete All");
    //        deleteAllButton.setToolTipText(REMOVE_ALL_SESSIONS);
    //        deleteAllButton.addActionListener(e -> deleteAll(keysList));
    //        panel.add(deleteAllButton);
    //        refreshView(keysList, true);
    //        enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, editButton, loadButton, deleteAllButton, renameButton);
    //        keysList.addListSelectionListener(e -> enableButtons(keysList.getItemsCount() != 0, exportButton, deleteButton, editButton, loadButton, deleteAllButton));
    //        return panel;
    //    }

    private void refreshView(JBList<String> keysList, boolean resetIndex) {
        int currentIndex = keysList.getSelectedIndex();
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(COMPONENT_SERVICE.getNodeHandler().getAllNodeNames());
        keysList.setModel(model);
        if (resetIndex) {
            currentIndex = Math.min(keysList.getItemsCount(), 0);
            keysList.setSelectedIndex(currentIndex);
        }
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

        keysList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        keysList.addListSelectionListener(e -> {
            showSelectedNodeContent(keysList);
        });

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

    @NotNull
    private MouseAdapter getMouseAdapter(KeyPopup keyPopup) {
        return new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    keyPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
    }

}