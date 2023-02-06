package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.HUMAN_DATE_FORMAT;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private Integer lineNumber;

    public SettingsView(@NotNull Project project) {
        this(project, null);
    }

    public SettingsView(@NotNull Project project, Integer lineNumber) {
        super(true); // use current window as parent
        this.project = project;
        this.lineNumber = lineNumber;
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

        this.keysList = new JBList<>(getKeyStringsFromPersistency().toArray(String[]::new));
        this.keysList.setPreferredSize(new Dimension(200, 600));
        KeyPopup keyPopup = new KeyPopup(this.keysList, this.project, resetIndex -> refreshView(this.keysList, resetIndex));
        keysList.addMouseListener(getMouseAdapter(keyPopup));
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
        refreshView(keysList, true);
        return dialogPanel;
    }

    private List<String> getKeyStringsFromPersistency() {
        if (this.lineNumber == null) {
            return COMPONENT_SERVICE.getNodeHandler().getAllNodeNames().stream().collect(Collectors.toList());
        }
        return COMPONENT_SERVICE //
                .getNodeHandler() //
                .getAllContainersPerNames() //
                .entrySet() //
                .stream() //
                .filter(entry -> entry.getValue().getLineNumber() == this.lineNumber) //
                .map(Map.Entry::getKey) //
                .collect(Collectors.toList());
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

    private void refreshView(JBList<String> keysList, boolean resetIndex) {
        int currentIndex = keysList.getSelectedIndex();
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addAll(getKeyStringsFromPersistency());
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