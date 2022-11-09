package org.intellij.sdk.project.model.components;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class SaveHandler implements ToolHandler{
    private final JLabel feedbackLabel;

    public void handle(DefaultTreeModel treeModel) {
    }

}
