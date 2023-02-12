package org.intellij.sdk.project.model.services;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;
import org.intellij.sdk.project.model.components.handlers.NodeHandler;
import org.intellij.sdk.project.model.components.handlers.SnapHandler;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import org.intellij.sdk.project.model.util.DebugContainerConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ComponentService {
    private final AtomicBoolean snapIsEnabled = new AtomicBoolean(false); // must change
    private final AtomicBoolean clearIsEnabled = new AtomicBoolean(false); // must change
    private final DebugTreeManager debugTreeManager = new DebugTreeManager(false); // must change
    private final JLabel feedbackMessage = new JLabel("Let's get started..."); // can be changed
    private final NodeHandler nodeHandler = new NodeHandler(); // can be static
    private final SnapHandler snapHandler = new SnapHandler(); // can be static
    private final DebugContainerConverter nodeConverter = new DebugContainerConverter(); // can be static

    @Setter
    private Optional<Integer> lastSelectedLeft = Optional.empty(); // must change
    @Setter
    private Optional<Integer> lastSelectedRight = Optional.empty(); // must change
    @Setter
    private String nodeNameInWindow = null; // must change
}
