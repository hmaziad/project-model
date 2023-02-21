package org.armadillo.core.components.views.settings;

import static org.armadillo.core.constants.TextConstants.EMPTY_STRING;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.*;
import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.components.views.DeleteAction;
import org.armadillo.core.components.views.DiffAction;
import org.armadillo.core.components.views.DiffView;
import org.armadillo.core.constants.MessageDialogues;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBList;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class KeyPopup extends JPopupMenu implements ReachServices {
    private final JBList<String> keysList;
    private final transient Project project;
    private final JLabel description;
    private final Integer lineNumber;
    private final JMenuItem rename = new JMenuItem("Rename");
    private final JMenuItem describe = new JMenuItem("Comment");
    private final JSeparator separator1 = new JSeparator();
    private final JSeparator separator2 = new JSeparator();
    private final JSeparator separator3 = new JSeparator();
    private final JMenuItem delete = new JMenuItem("Delete");
    private final JMenuItem load = new JMenuItem("Load");
    private final JMenuItem export = new JMenuItem("Export");
    private final JMenuItem doImport = new JMenuItem("Import");
    private final JMenuItem deleteAll = new JMenuItem("Delete All");
    private final JMenuItem diff = new JMenuItem("Diff Sessions");

    public KeyPopup(JBList<String> keysList, Project project, JLabel description, Integer lineNumber) {
        this.keysList = keysList;
        this.project = project;
        this.description = description;
        this.lineNumber = lineNumber;
        addActionListeners();
        addActions();
        initKeyHandler();
    }

    private void addActionListeners() {
        this.rename.addActionListener(e -> rename(this.keysList));
        this.describe.addActionListener(e -> describe(this.keysList));
        this.delete.addActionListener(e -> delete(this.keysList));
        this.load.addActionListener(e -> load(this.keysList));
        this.export.addActionListener(e -> export(this.keysList));
        this.doImport.addActionListener(e -> doImport());
        this.deleteAll.addActionListener(e -> deleteAll());
        this.diff.addActionListener(e -> diff(this.keysList));
    }

    private void addActions() {
        add(this.rename);
        add(this.describe);
        add(this.load);
        add(this.separator1);
        add(this.diff);
        add(this.separator3);
        add(this.export);
        add(this.doImport);
        add(this.separator2);
        add(this.delete);
        add(this.deleteAll);
    }

    private void initKeyHandler() {
        InputMap inputMap = this.keysList.getInputMap(JComponent.WHEN_FOCUSED);
        KeyStroke diffKeys = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);
        KeyStroke deleteKeys = KeyStroke.getKeyStroke("DELETE");
        inputMap.put(diffKeys, "diff");
        inputMap.put(deleteKeys, "delete");
        this.keysList.getActionMap().put("diff", new DiffAction(() -> diff(keysList)));
        this.keysList.getActionMap().put("delete", new DeleteAction(() -> delete(keysList)));
    }

    @Override
    public void show(Component invoker, int x, int y) {
        int totalSelectedKeys = keysList.getSelectedIndices().length;
        int allKeys = keysList.getItemsCount();

        this.rename.setVisible(false);
        this.describe.setVisible(false);
        this.separator1.setVisible(false);
        this.load.setVisible(false);
        this.export.setVisible(false);
        this.separator2.setVisible(false);
        this.delete.setVisible(false);
        this.deleteAll.setVisible(false);
        this.diff.setVisible(false);
        this.separator3.setVisible(false);

        this.doImport.setVisible(true);
        if (allKeys > 0) {
            this.deleteAll.setVisible(true);
            this.separator2.setVisible(true);
        }

        if (totalSelectedKeys > 0) {
            this.export.setVisible(true);
            this.delete.setVisible(true);
            this.describe.setVisible(true);
            this.separator1.setVisible(true);
        }

        if (totalSelectedKeys == 1) {
            this.rename.setVisible(true);
            this.load.setVisible(true);
        }

        if (totalSelectedKeys == 2) {
            this.diff.setVisible(true);
            this.separator3.setVisible(true);
        }

        super.show(invoker, x, y);
    }

    private void doImport() {
        nodeHandler.doImport(this.project);
        reloadNodeNames();
    }

    private void reloadNodeNames() {
        LOG.info("Reloading node names");
        int selectedIndex = this.keysList.getSelectedIndex();
        DefaultListModel<String> updatedNodeNames = new DefaultListModel<>();
        List<String> sortedNodeNames;
        if (Objects.nonNull(this.lineNumber)) {
            sortedNodeNames = nodeHandler.getSortedNodeNames(this.lineNumber);
        }else {
            sortedNodeNames = nodeHandler.getSortedNodeNames();
        }
        updatedNodeNames.addAll(sortedNodeNames);
        this.keysList.setModel(updatedNodeNames);
        int itemsCount = this.keysList.getItemsCount();
        if (selectedIndex >= itemsCount && itemsCount > 0) {
            this.keysList.setSelectedIndex(itemsCount - 1);
        } else {
            this.keysList.setSelectedIndex(selectedIndex);
        }
    }


    private void export(JBList<String> keysList) {
        nodeHandler.export(getSelectedItems(keysList), this.project);
    }

    private void load(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        DebugNodeContainer nodeContainer = nodeHandler.getNodeContainerByName(selectedKey).orElseThrow();
        treeHandler.setNodeNameInWindow(selectedKey, this.project);
        treeHandler.getDebugTreeManager(this.project).setRoot(nodeContainer.getNode());
    }

    private void deleteAll() {
        Enumeration<String> elements = ((DefaultListModel<String>) this.keysList.getModel()).elements();
        boolean isSure = nodeHandler.delete(Collections.list(elements), this.project);
        if (isSure) {
            reloadNodeNames();
        }
    }

    private void rename(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedNodeName, false);
        while (nodeHandler.getAllContainersPerNames().containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            newNodeName = newNodeName.replace(' ', '_');
            nodeHandler.renameNode(selectedNodeName, newNodeName);
            reloadNodeNames();
        }
    }

    private void describe(JBList<String> keysList) {
        List<String> selectedItems = getSelectedItems(keysList);
        StringBuilder sessionsMessage = new StringBuilder();
        selectedItems.forEach(item -> sessionsMessage.append("\n - ").append(item));
        String inputDescription = MessageDialogues.getDescriptionDialogue(this.project, sessionsMessage.toString(), EMPTY_STRING);
        if (StringUtil.isNotEmpty(inputDescription)) {
            selectedItems //
                .stream() //
                .map(nodeHandler::getNodeContainerByName) //
                .forEach(container -> container.ifPresent(debugNodeContainer -> debugNodeContainer.setDescription(inputDescription)));
            this.description.setText(inputDescription);
        }
    }

    private void delete(JBList<String> keysList) {
        List<String> selectedValues = getSelectedItems(keysList);
        boolean isSure = nodeHandler.delete(selectedValues, this.project);
        if (isSure) {
            reloadNodeNames();
        }
    }

    @NotNull
    private List<String> getSelectedItems(JBList<String> keysList) {
        return Arrays //
            .stream(keysList.getSelectedIndices()) //
            .mapToObj(index -> keysList.getModel().getElementAt(index)) //
            .collect(Collectors.toList());
    }

    private void diff(JBList<String> keysList) {
        DiffView diffView = new DiffView(this.project);
        List<String> selectedItems = getSelectedItems(keysList);
        if (selectedItems.size() == 2) {
            diffView.setSelectedNode(selectedItems.get(0), true);
            diffView.setSelectedNode(selectedItems.get(1), false);
            diffView.showAndGet();
        }
    }
}
