package org.intellij.sdk.project.model.components.handlers;

import org.intellij.sdk.project.model.util.DebugContainerConverter;

public interface ReachServices {
    NodeHandler nodeHandler = new NodeHandler();
    DebugContainerConverter nodeConverter = new DebugContainerConverter();
    SnapHandler snapHandler = new SnapHandler();
    TreeHandler treeHandler = new TreeHandler();
    SelectionHandler selectionHandler = new SelectionHandler();
}
