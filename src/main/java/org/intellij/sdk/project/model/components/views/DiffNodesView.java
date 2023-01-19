package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.DIFF_NODES;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import org.intellij.sdk.project.model.tree.components.DebugTreeRenderer;
import org.intellij.sdk.project.model.components.handlers.DiffHandler;
import org.intellij.sdk.project.model.components.handlers.DropdownHandler;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import icons.SdkIcons;

public class DiffNodesView extends DialogWrapper {
    private final Project project;
    private final DropdownHandler dropdownHandler;
    private final DiffHandler diffHandler = new DiffHandler();
    private final JButton diffButton = new JButton();

    public DiffNodesView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        this.dropdownHandler = new DropdownHandler(project);
        setTitle(DIFF_NODES);
        init();
        initButton();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JComboBox<String> leftDropdown = new ComboBox<>();
        JComboBox<String> rightDropdown = new ComboBox<>();
        this.dropdownHandler.addNodesToDropdown(leftDropdown);
        this.dropdownHandler.addNodesToDropdown(rightDropdown);
        addDiffBtnListener(leftDropdown, rightDropdown);
        return createDiffPanel(leftDropdown, rightDropdown);
    }

    @NotNull
    private JPanel createDiffPanel(JComboBox<String> leftDropdown, JComboBox<String> rightDropdown) {
        setSize(1300, 1100);
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        dialogPanel.add(createNodesPanel(leftDropdown), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        dialogPanel.add(this.diffButton, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.weightx = 1;
        dialogPanel.add(createNodesPanel(rightDropdown), gbc);
        return dialogPanel;
    }

    private JPanel createNodesPanel(JComboBox<String> nodesDropdown) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 100));
        scrollPane.setViewportView(new JTextArea());
        mainPanel.add(nodesDropdown, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        showSelectedNodeContent(nodesDropdown, scrollPane);
        nodesDropdown.addActionListener(e -> showSelectedNodeContent(nodesDropdown, scrollPane));
        return mainPanel;
    }

    private void showSelectedNodeContent(JComboBox<String> nodesDropdown, JScrollPane scrollPane) {
        DebugNode selectedNode = this.dropdownHandler.getSelectedNode(nodesDropdown);
        JTree debugTree = new Tree(); // this tree should come predefined, we will work it now
        debugTree.setRootVisible(false);
        debugTree.setCellRenderer(new DebugTreeRenderer());
        DefaultTreeModel localTreeModel = (DefaultTreeModel) debugTree.getModel();
        localTreeModel.setRoot(selectedNode);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(debugTree);
        scrollPane.setViewportView(panel);
    }

    private void initButton() {
        this.diffButton.setIcon(SdkIcons.DIFF_SCALED);
        this.diffButton.getModel().addChangeListener(e -> diffButton.setContentAreaFilled(diffButton.getModel().isRollover()));
        this.diffButton.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private void addDiffBtnListener(JComboBox<String> leftDropdown, JComboBox<String> rightDropdown) {
        this.diffButton.addActionListener(e -> {
            DebugNode leftNode = this.dropdownHandler.getSelectedNode(leftDropdown);
            String leftNodeName = this.dropdownHandler.getSelectedNodeName(leftDropdown);
            DebugNode rightNode = this.dropdownHandler.getSelectedNode(rightDropdown);
            String rightNodeName = this.dropdownHandler.getSelectedNodeName(rightDropdown);
            this.diffHandler.diffNodes(leftNode, leftNodeName, rightNode, rightNodeName, this.project);
        });
    }

}