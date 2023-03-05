package org.armadillo.core.components.handlers;

import org.armadillo.core.util.DebugContainerConverter;

public interface ReachSomeServices {
    DebugContainerConverter nodeConverter = new DebugContainerConverter();
    SnapHandler snapHandler = new SnapHandler();
    TreeHandler treeHandler = new TreeHandler();
    SelectionHandler selectionHandler = new SelectionHandler();
}
