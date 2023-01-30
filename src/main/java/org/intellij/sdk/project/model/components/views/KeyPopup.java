package org.intellij.sdk.project.model.components.views;

import java.awt.*;

import javax.swing.*;
import com.intellij.ui.components.JBList;

public class KeyPopup extends JPopupMenu {
    private final JBList<String> keysList;
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

    public KeyPopup(JBList<String> keysList) {
        this.keysList = keysList;
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
}
