package org.intellij.sdk.project.model.services;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;

import com.intellij.ui.treeStructure.Tree;

import lombok.Getter;

@Getter
public class ComponentService {
    private final AtomicBoolean snapIsEnabled = new AtomicBoolean(false);
    private final AtomicBoolean clearIsEnabled = new AtomicBoolean(false);
    private final JTree debugTree = new Tree();
    private final JLabel feedbackMessage = new JLabel("Let's get started...");
}
