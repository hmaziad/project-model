package org.intellij.sdk.project.model.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import lombok.Value;

@Value
public class ExpandTreeHandler implements ToolHandler {
    JTree tree;

    @Override
    public void handle(DefaultTreeModel treeModel) {
        for(int i = 0; i < this.tree.getRowCount(); i++){
            this.tree.expandRow(i);
        }
    }

}
