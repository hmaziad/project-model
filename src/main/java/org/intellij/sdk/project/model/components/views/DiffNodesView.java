package org.intellij.sdk.project.model.components.views;

import static org.intellij.sdk.project.model.constants.TextConstants.DIFF_NODES;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.intellij.sdk.project.model.components.handlers.DiffHandler;
import org.intellij.sdk.project.model.components.handlers.DropdownHandler;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;

import icons.SdkIcons;

public class DiffNodesView extends DialogWrapper implements ReachServices {
    private final Project project;
    private final DropdownHandler dropdownHandler;
    private final DiffHandler diffHandler = new DiffHandler();
    private final JButton diffButton = new JButton();
    private DebugTreeManager leftDebugTreeManager;
    private DebugTreeManager rightDebugTreeManager;
    private JComboBox<String> leftDropdown;
    private JComboBox<String> rightDropdown;

    public DiffNodesView(@NotNull Project project) {
        super(true); // use current window as parent
        this.project = project;
        this.dropdownHandler = new DropdownHandler(project);
        setTitle(DIFF_NODES);
        init();
        initButton();
    }

    public void setSelectedNode(String nodeName, boolean isLeft) {
        if (isLeft) {
            this.leftDropdown.setSelectedItem(nodeName);
        } else {
            this.rightDropdown.setSelectedItem(nodeName);
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.leftDropdown = new ComboBox<>();
        this.rightDropdown = new ComboBox<>();
        this.dropdownHandler.addNodesToDropdown(leftDropdown, COMPONENT_SERVICE.getLastSelectedLeft());
        this.dropdownHandler.addNodesToDropdown(rightDropdown, COMPONENT_SERVICE.getLastSelectedRight());

        leftDropdown.addActionListener(e -> {
            this.leftDebugTreeManager.clearNodeColors();
            COMPONENT_SERVICE.setLastSelectedLeft(Optional.of(leftDropdown.getSelectedIndex()));
            doDiff(leftDropdown, rightDropdown, false);
        });
        rightDropdown.addActionListener(e -> {
            this.rightDebugTreeManager.clearNodeColors();
            COMPONENT_SERVICE.setLastSelectedRight(Optional.of(rightDropdown.getSelectedIndex()));
            doDiff(leftDropdown, rightDropdown, false);
        });

        addDiffBtnListener(leftDropdown, rightDropdown);
        JPanel diffPanel = createDiffPanel(leftDropdown, rightDropdown);
        doDiff(leftDropdown, rightDropdown, false);
        return diffPanel;
    }

    @NotNull
    private JPanel createDiffPanel(JComboBox<String> leftDropdown, JComboBox<String> rightDropdown) {
        setSize(1300, 1100);
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.leftDebugTreeManager = new DebugTreeManager(true);
        this.rightDebugTreeManager = new DebugTreeManager(true);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        dialogPanel.add(createNodesPanel(leftDropdown, leftDebugTreeManager), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        dialogPanel.add(this.diffButton, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.weightx = 1;
        dialogPanel.add(createNodesPanel(rightDropdown, rightDebugTreeManager), gbc);
        return dialogPanel;
    }

    private JPanel createNodesPanel(JComboBox<String> nodesDropdown, DebugTreeManager debugTreeManager) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 100));
        scrollPane.setViewportView(new JTextArea());
        mainPanel.add(nodesDropdown, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        showSelectedNodeContent(nodesDropdown, scrollPane, debugTreeManager);
        nodesDropdown.addActionListener(e -> showSelectedNodeContent(nodesDropdown, scrollPane, debugTreeManager));
        return mainPanel;
    }

    private void showSelectedNodeContent(JComboBox<String> nodesDropdown, JScrollPane scrollPane, DebugTreeManager debugTreeManager) {
        DebugNodeContainer selectedContainer = this.dropdownHandler.getSelectedContainer(nodesDropdown);
        debugTreeManager.setRoot(selectedContainer.getNode());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(debugTreeManager.getDebugTree());
        scrollPane.setViewportView(panel);
    }

    private void initButton() {
        this.diffButton.setIcon(SdkIcons.DIFF_SCALED);
        this.diffButton.getModel().addChangeListener(e -> diffButton.setContentAreaFilled(diffButton.getModel().isRollover()));
        this.diffButton.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private void addDiffBtnListener(JComboBox<String> leftDropdown, JComboBox<String> rightDropdown) {
        this.diffButton.addActionListener(e -> {
            doDiff(leftDropdown, rightDropdown, true);
        });
    }

    private void doDiff(JComboBox<String> leftDropdown, JComboBox<String> rightDropdown, boolean showDiffIntellij) {
        DebugNodeContainer leftNodeContainer = this.dropdownHandler.getSelectedContainer(leftDropdown);
        String leftNodeName = this.dropdownHandler.getSelectedNodeName(leftDropdown);
        DebugNodeContainer rightNodeContainer = this.dropdownHandler.getSelectedContainer(rightDropdown);
        String rightNodeName = this.dropdownHandler.getSelectedNodeName(rightDropdown);
        List<List<List<Integer>>> changes =
            this.diffHandler.diffNodes(leftNodeContainer.getNode(), leftNodeName, rightNodeContainer.getNode(), rightNodeName, showDiffIntellij, this.project);
        List<List<Integer>> additions = changes.get(0);
        List<List<Integer>> deletions = changes.get(1);
        List<List<Integer>> modifications1 = changes.get(2);
        List<List<Integer>> modifications2 = changes.get(3);

        this.leftDebugTreeManager.addDiffDeletions(deletions);
        this.leftDebugTreeManager.addDiffModifications(modifications1);

        this.rightDebugTreeManager.addDiffInsertions(additions);
        this.rightDebugTreeManager.addDiffModifications(modifications2);
        this.leftDebugTreeManager.getDebugTree().repaint();
        this.rightDebugTreeManager.getDebugTree().repaint();
    }

}