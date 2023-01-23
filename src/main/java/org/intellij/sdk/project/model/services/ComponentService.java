package org.intellij.sdk.project.model.services;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import org.intellij.sdk.project.model.components.handlers.NodeHandler;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ComponentService {
    private final AtomicBoolean snapIsEnabled = new AtomicBoolean(false);
    private final AtomicBoolean clearIsEnabled = new AtomicBoolean(false);
    private final DebugTreeManager debugTreeManager = new DebugTreeManager();
    private final JLabel feedbackMessage = new JLabel("Let's get started...");
    private final NodeHandler nodeHandler = new NodeHandler();
    @Setter
    private Optional<Integer> lastSelectedLeft = Optional.empty();
    @Setter
    private Optional<Integer> lastSelectedRight = Optional.empty();
    @Setter
    private String nodeNameInWindow = null;
}
