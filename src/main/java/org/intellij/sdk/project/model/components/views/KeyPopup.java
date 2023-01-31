package org.intellij.sdk.project.model.components.views;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.*;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;

public class KeyPopup extends JPopupMenu implements ReachServices {
    private final JBList<String> keysList;
    private final Project project;
    private final Consumer<Boolean> refreshViewWithReset;
    private final JMenuItem rename = new JMenuItem("Rename");
    private final JMenuItem describe = new JMenuItem("Describe");
    private final JSeparator separator1 = new JSeparator();
    private final JSeparator separator2 = new JSeparator();
    private final JSeparator separator3 = new JSeparator();
    private final JMenuItem delete = new JMenuItem("Delete");
    private final JMenuItem load = new JMenuItem("Load");
    private final JMenuItem export = new JMenuItem("Export");
    private final JMenuItem doImport = new JMenuItem("Import");
    private final JMenuItem deleteAll = new JMenuItem("Delete All");
    private final JMenuItem diff = new JMenuItem("Diff Sessions");

    public KeyPopup(JBList<String> keysList, Project project, Consumer<Boolean> refreshViewWithReset) {
        this.keysList = keysList;
        this.project = project;
        this.refreshViewWithReset = refreshViewWithReset;
        this.rename.addActionListener(e -> rename(this.keysList));
        this.describe.addActionListener(e -> describe(this.keysList));
        this.delete.addActionListener(e -> delete(this.keysList));
        this.load.addActionListener(e -> load(this.keysList));
        this.export.addActionListener(e -> export(this.keysList));
        this.doImport.addActionListener(e -> doImport());
        this.deleteAll.addActionListener(e -> deleteAll());
        this.diff.addActionListener(e -> diff(this.keysList));

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

        initKeyHandler();
    }

    private void initKeyHandler() {
        InputMap inputMap = this.keysList.getInputMap(JComponent.WHEN_FOCUSED);
        //Ctrl-b to go backward one character
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
        COMPONENT_SERVICE.getNodeHandler().doImport(this.project);
        this.refreshViewWithReset.accept(true);
    }

    private void export(JBList<String> keysList) {
        COMPONENT_SERVICE.getNodeHandler().export(getSelectedItems(keysList), this.project);
    }

    private void load(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        DebugNodeContainer nodeContainer = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedKey).orElseThrow();
        COMPONENT_SERVICE.setNodeNameInWindow(selectedKey);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(nodeContainer.getNode());
    }

    private void deleteAll() {
        boolean isSure = COMPONENT_SERVICE.getNodeHandler().deleteAll(this.project);
        if (isSure) {
            COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
            COMPONENT_SERVICE.setNodeNameInWindow(null);
            this.refreshViewWithReset.accept(true);
        }
    }

    private void rename(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        String newNodeName = MessageDialogues.getRenameDialogue(this.project, selectedNodeName, false);
        while (COMPONENT_SERVICE.getNodeHandler().getAllContainersPerNames().containsKey(newNodeName)) {
            newNodeName = MessageDialogues.getRenameDialogue(this.project, newNodeName, true);
        }
        if (Objects.nonNull(newNodeName)) {
            newNodeName = newNodeName.replace(' ', '_');
            COMPONENT_SERVICE.getNodeHandler().renameNode(selectedNodeName, newNodeName);
            this.refreshViewWithReset.accept(false);
        }
    }

    private void describe(JBList<String> keysList) {
        List<String> selectedItems = getSelectedItems(keysList);
        if (selectedItems.size() == 1) {
            String selectedNodeName = keysList.getSelectedValue();
            Optional<DebugNodeContainer> container = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedNodeName);
            String currentDescription = null;
            if (container.isPresent()) {
                currentDescription = container.get().getDescription();
            }
            String description = MessageDialogues.getDescriptionDialogue(this.project, "\n - " + selectedNodeName, currentDescription);
            if (Objects.nonNull(description)) {
                container.ifPresent(debugNodeContainer -> debugNodeContainer.setDescription(description));
                this.refreshViewWithReset.accept(false);
            }
        } else {
            StringBuilder sessionsMessage = new StringBuilder();
            selectedItems.forEach(item -> sessionsMessage.append("\n - ").append(item));

            String description = MessageDialogues.getDescriptionDialogue(this.project, sessionsMessage.toString(), "");
            if (Objects.nonNull(description)) {
                selectedItems //
                    .stream() //
                    .map(nodeName -> COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(nodeName)) //
                    .forEach(container -> container.ifPresent(debugNodeContainer -> debugNodeContainer.setDescription(description)));
                this.refreshViewWithReset.accept(false);
            }
        }
    }

    private void delete(JBList<String> keysList) {
        List<String> selectedValues = getSelectedItems(keysList);
        boolean isSure = COMPONENT_SERVICE.getNodeHandler().delete(selectedValues, this.project);
        if (isSure) {
            this.refreshViewWithReset.accept(true);
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
        DiffNodesView diffView = new DiffNodesView(this.project);
        List<String> selectedItems = getSelectedItems(keysList);
        if (selectedItems.size() == 2) {
            diffView.setSelectedNode(selectedItems.get(0), true);
            diffView.setSelectedNode(selectedItems.get(1), false);
            diffView.showAndGet();
        }
    }
}
