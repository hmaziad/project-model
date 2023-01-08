package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.services.ParserService.convertNodeToString;

import java.awt.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.intellij.sdk.project.model.components.DropdownObserver;
import org.intellij.sdk.project.model.components.handlers.DiffRefHandler;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

public class DiffNodesView extends DialogWrapper {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private Project project;
    private JButton diffRefButton;

    public DiffNodesView(@NotNull Project project, JButton diffRefButton) {
        super(true); // use current window as parent
        this.project = project;
        this.diffRefButton = diffRefButton;
        setTitle("Diff Saved Nodes");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Map<String, DebugNode> nodes = persistencyService.getNodes();

        JComboBox<String> leftSnapsDropdown = new ComboBox<>();
        DropdownObserver leftDropdownObserver = new DropdownObserver(leftSnapsDropdown);
        leftSnapsDropdown.setSelectedIndex(0);

        JComboBox<String> rightSnapsDropdown = new ComboBox<>();
        DropdownObserver rightDropdownObserver = new DropdownObserver(rightSnapsDropdown);
        rightSnapsDropdown.setSelectedIndex(nodes.size() > 1 ? 1 : 0);

        setSize(1300, 1100);
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        dialogPanel.add(getNodesPanel(leftDropdownObserver), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        dialogPanel.add(this.diffRefButton, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.weightx = 1;
        dialogPanel.add(getNodesPanel(rightDropdownObserver), gbc);

        this.diffRefButton.setBorder(new EmptyBorder(10,20,10,20));
        this.diffRefButton.addActionListener(e -> {
            DiffRefHandler diffRefHandler = new DiffRefHandler(this.project, leftDropdownObserver, rightDropdownObserver);
            diffRefHandler.handle(null);
        });
        return dialogPanel;
    }

    private JPanel getNodesPanel(DropdownObserver dropdownObserver) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 100));
        scrollPane.setViewportView(new JTextArea());
        JComboBox<String> jComboBox = dropdownObserver.getJComboBox();
        showSelectedNodeContent(dropdownObserver, scrollPane);
        jComboBox.addActionListener(e -> showSelectedNodeContent(dropdownObserver, scrollPane));
        mainPanel.add(jComboBox, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private void showSelectedNodeContent(DropdownObserver dropdownObserver, JScrollPane scrollPane) {
        String selectedItem = dropdownObserver.getCurrentItem();
        DebugNode debugNode = persistencyService.getNodes().get(selectedItem);
        String debugNodeString = convertNodeToString(debugNode);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(debugNodeString);
        scrollPane.setViewportView(textArea);
    }

}