package org.intellij.sdk.project.model;

import com.intellij.xdebugger.frame.XValueContainer;

public class ComputeChildrenService implements Task {
    private final XValueContainer container;

    public ComputeChildrenService(XValueContainer container) {
        this.container = container;
    }

    @Override
    public void execute() {
        //        XTestCompositeNode node = new XTestCompositeNode(null);
        //        frame.computeChildren(node);
    }
}
