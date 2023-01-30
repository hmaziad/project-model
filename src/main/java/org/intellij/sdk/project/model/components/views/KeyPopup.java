package org.intellij.sdk.project.model.components.views;

import java.awt.*;
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
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;

public class KeyPopup extends JPopupMenu implements ReachServices {
    private final JBList<String> keysList;
    private final Project project;
    private final Consumer<Boolean> resetIndex;
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

    public KeyPopup(JBList<String> keysList, Project project, Consumer<Boolean> resetIndex) {
        this.keysList = keysList;
        this.project = project;
        this.resetIndex = resetIndex;
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
        this.resetIndex.accept(true);
    }

    private void export(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        COMPONENT_SERVICE.getNodeHandler().export(selectedKey, this.project);
    }

    private void load(JBList<String> keysList) {
        String selectedKey = keysList.getSelectedValue();
        DebugNodeContainer nodeContainer = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedKey).orElseThrow();
        COMPONENT_SERVICE.setNodeNameInWindow(selectedKey);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(nodeContainer.getNode());
    }

    private void deleteAll() {
        COMPONENT_SERVICE.getNodeHandler().deleteAll(this.project);
        COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
        COMPONENT_SERVICE.setNodeNameInWindow(null);
        this.resetIndex.accept(true);
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
            this.resetIndex.accept(false);
        }
    }

    private void describe(JBList<String> keysList) {
        String selectedNodeName = keysList.getSelectedValue();
        Optional<DebugNodeContainer> container = COMPONENT_SERVICE.getNodeHandler().getNodeContainerByName(selectedNodeName);
        String currentDescription = null;
        if (container.isPresent()) {
            currentDescription = container.get().getDescription();
        }
        String description = MessageDialogues.getEditDialogue(this.project, selectedNodeName, currentDescription);
        if (Objects.nonNull(description)) {
            container.ifPresent(debugNodeContainer -> debugNodeContainer.setDescription(description));
            this.resetIndex.accept(false);
        }
    }

    private void delete(JBList<String> keysList) {
        int[] selectedIndices = keysList.getSelectedIndices();
        List<String> selectedValues = Arrays //
            .stream(selectedIndices) //
            .mapToObj(index -> keysList.getModel().getElementAt(index)) //
            .collect(Collectors.toList());

        COMPONENT_SERVICE.getNodeHandler().delete(selectedValues, this.project);
        this.resetIndex.accept(true);
    }

    private void diff(JBList<String> keysList) {
        // nothing for now
    }

}
