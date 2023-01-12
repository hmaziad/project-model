package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.components.DropdownObserver.CURRENT_DEBUGGER_SESSION;

import java.awt.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.DebuggerTreeRenderer;
import org.intellij.sdk.project.model.components.DropdownObserver;
import org.intellij.sdk.project.model.components.handlers.DiffRefHandler;
import org.intellij.sdk.project.model.components.handlers.SnapHandler;
import org.intellij.sdk.project.model.services.PersistencyService;
import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

public class DiffNodesView extends DialogWrapper {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private Project project;
    private JButton scaledDiffButton;
    private DebugNode currentSession;

    public DiffNodesView(@NotNull Project project, JButton scaledDiffButton) {
        super(true); // use current window as parent
        this.project = project;
        this.scaledDiffButton = scaledDiffButton;
        setTitle("Diff Saved Nodes");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() { //todo handle no saved nodes or current debuggin session
        Map<String, DebugNode> nodes = persistencyService.getNodes();
        SnapHandler snapHandler = new SnapHandler(this.project, new JLabel(), session -> this.currentSession = session);
        snapHandler.handle(null);

        JComboBox<String> leftSnapsDropdown = new ComboBox<>();
        DropdownObserver leftDropdownObserver = new DropdownObserver(leftSnapsDropdown);
        leftDropdownObserver.addCurrentSession(this.currentSession);
        leftSnapsDropdown.setSelectedIndex(0);

        JComboBox<String> rightSnapsDropdown = new ComboBox<>();
        DropdownObserver rightDropdownObserver = new DropdownObserver(rightSnapsDropdown);
        rightDropdownObserver.addCurrentSession(this.currentSession);
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

        dialogPanel.add(this.scaledDiffButton, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.weightx = 1;
        dialogPanel.add(getNodesPanel(rightDropdownObserver), gbc);

        this.scaledDiffButton.setBorder(new EmptyBorder(10,20,10,20));
        this.scaledDiffButton.addActionListener(e -> {
            DiffRefHandler diffRefHandler = new DiffRefHandler(this.project, leftDropdownObserver, rightDropdownObserver, this.currentSession);
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
        DebugNode debugNode;
        if (selectedItem.equals(CURRENT_DEBUGGER_SESSION)) {
            debugNode = this.currentSession;
        } else {
            debugNode = persistencyService.getNodes().get(selectedItem);
        }
        JTree debugTree = new Tree();
        debugTree.setRootVisible(false);
        debugTree.setCellRenderer(new DebuggerTreeRenderer());
        DefaultTreeModel localTreeModel = (DefaultTreeModel) debugTree.getModel();
        localTreeModel.setRoot(debugNode);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(debugTree);
        scrollPane.setViewportView(panel);
    }

}