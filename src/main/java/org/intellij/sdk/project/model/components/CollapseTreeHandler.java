package org.intellij.sdk.project.model.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import lombok.Value;

@Value
public class CollapseTreeHandler implements ToolHandler {
    JTree tree;
    @Override
    public void handle(DefaultTreeModel treeModel) {
        for(int i = this.tree.getRowCount() - 1; i >= 0; i--){
            this.tree.collapseRow(i);
        }
    }
}
