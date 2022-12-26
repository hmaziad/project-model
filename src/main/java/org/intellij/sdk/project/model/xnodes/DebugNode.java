package org.intellij.sdk.project.model.xnodes;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.jetbrains.annotations.VisibleForTesting;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueContainerNode;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueGroupNodeImpl;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(force = true)
public class DebugNode extends DefaultMutableTreeNode {
    private final String text;
    @VisibleForTesting
    @Setter
    private List<DebugNode> myChildren = new ArrayList<>();

    public DebugNode(String text) {
        this.text = text;
    }

    public static DebugNode createNode(String text) {
        return new DebugNode(text);
    }

    public DebugNode(XValueContainerNode xNode) {
        this.text = xNode.getText().toString();
        List children = xNode.getLoadedChildren();
        for (var child : children) {
            if (child instanceof XValueNodeImpl) {
                XValueNodeImpl xChild = (XValueNodeImpl) child;
                DebugNode debugNode = new DebugNode(xChild);
                add(debugNode);
            } else if (child instanceof XValueGroupNodeImpl) {
                XValueGroupNodeImpl xChild = (XValueGroupNodeImpl) child;
                DebugNode debugNode = new DebugNode(xChild.getText().toString());
                add(debugNode);
            } else {
                throw new IllegalStateException("Could not cast node of class " + child.getClass());
            }
        }
    }

    public void add(DebugNode newChild) {
        this.myChildren.add(newChild);
        add((MutableTreeNode) newChild);
    }

    @Override
    public String toString() {
        return this.text;
    }

}
