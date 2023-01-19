// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.intellij.sdk.project.model.components.SettingsPanel;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.listeners.DebuggerTreeModelListener;
import com.intellij.openapi.project.Project;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class DebuggerWindow implements ReachServices {

    private JPanel debuggerWindowContent;

    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/360006504879-Add-an-action-buttons-to-my-custom-tool-window
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/360009445759-How-to-create-button-with-icon
    public DebuggerWindow(Project project) {
        this.debuggerWindowContent = new SettingsPanel(true);

        JTree debugTree = COMPONENT_SERVICE.getDebugTree();
        debugTree.setRootVisible(false);
        debugTree.getModel().addTreeModelListener(new DebuggerTreeModelListener());
        debugTree.setCellRenderer(new DebuggerTreeRenderer());
        this.debuggerWindowContent.add(debugTree);
        //        this.snapButton.setEnabled(false);
        //        this.clearButton.setEnabled(false);
        //        buttonEnablingService.setSnapButton(this.snapButton);
        //        buttonEnablingService.setClearButton(this.clearButton);
        //        Presentation presentation = new Presentation("Hello");
        //        MyButton myButton = new MyButton("OKay", "Meshe", SdkIcons.CLEAR_ICON,presentation);
        //        presentation.setIcon(SdkIcons.EXPAND_ICON);
        //        presentation.setDisabledIcon(SdkIcons.COLLAPSE_ICON); // THESE ARE ENOUGH!!!!!
        //        ActionButton actionButton = new ActionButton(myButton, presentation, ActionPlaces.UNKNOWN, new Dimension(10, 10));
        //        this.toolbar.add(actionButton);
        //
        //        final ActionManager actionManager = ActionManager.getInstance();
        //        DefaultActionGroup actionGroup = new DefaultActionGroup("ACTION_GROUP", false);
        //        actionGroup.add(ActionManager.getInstance().getAction("deployAction"));
        //        ActionToolbar actionToolbar = actionManager.createActionToolbar("ACTION_TOOLBAR", actionGroup, true);
        //        actionToolbar.setOrientation(SwingConstants.HORIZONTAL);
        //        debuggerWindowContent.setToolbar(actionToolbar.getComponent());

        //        testButton.addActionListener(e -> presentation.setEnabled(!presentation.isEnabled()));
        //        JButton scaledDiffButton = new JButton();
        //        this.feedbackLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        //        DefaultTreeModel treeModel = (DefaultTreeModel) this.debugTree.getModel();
        //        treeModel.setRoot(null);
        //        final TreePopup treePopup = new TreePopup(this.debugTree);
        //        debugTree.addMouseListener(new MouseAdapter() {
        //            public void mouseReleased(MouseEvent e) {
        //                if (SwingUtilities.isRightMouseButton(e)) {
        //                    int row = debugTree.getRowForLocation(e.getX(), e.getY());
        //                    debugTree.setSelectionRow(row);
        //                    if (row >= 0) {
        //                        treePopup.setRow(row);
        //                        treePopup.show(e.getComponent(), e.getX(), e.getY());
        //                    }
        //                }
        //            }
        //        });
        //        ButtonHandler buttonHandler = new ButtonHandler();
        //        // toolbar
        //        buttonHandler.handleToolbar(this.toolbar);
        //        // icon buttons in toolbar
        //        buttonHandler.handleButton(this.snapButton, ButtonType.SNAP);
        //        buttonHandler.handleButton(this.diffButton, ButtonType.DIFF);
        //        buttonHandler.handleButton(this.clearButton, ButtonType.CLEAR);
        //        buttonHandler.handleButton(scaledDiffButton, ButtonType.DIFF_SCALED);
        //        buttonHandler.handleButton(this.viewSavedNodesButton, ButtonType.VIEW_NODES);
        //
        //        // toolbar separators
        //        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorCore);
        //        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorOther);
        //        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorFeedback);
        //        buttonHandler.handleToolbarSeperator(this.toolbarSeparatorSnap);
        //
        //        // components
        //
        //        SnapHandler snapHandler = new SnapHandler(project, this.feedbackLabel, treeModel::setRoot);
        //        ToolHandler clearHandler = new ClearHandler(this.feedbackLabel);
        //        SaveHandler saveHandler = new SaveHandler(this.feedbackLabel);
        //        DeleteHandler deleteHandler = new DeleteHandler(this.feedbackLabel, project);
        //        this.debugTree.setRootVisible(false);
        //        this.debugTree.setCellRenderer(new DebuggerTreeRenderer());
        //        this.debugTree
        //            .getModel()
        //            .addTreeModelListener(new DebuggerTreeModelListener(this.feedbackLabel));
        //
        //        // action listeners
        //
        //        this.clearButton.addActionListener(e -> clearHandler.handle(treeModel));
        //        this.snapButton.addActionListener(e -> {
        //            snapHandler.handle(treeModel);
        //            saveHandler.handle(treeModel);
        //        });
        //        this.diffButton.addActionListener(e -> );
        //        this.viewSavedNodesButton.addActionListener(e -> new SettingsNodesView(project,saveHandler, deleteHandler, treeModel).showAndGet());
    }

    class TreePopup extends JPopupMenu {
        private int row;

        public TreePopup(JTree tree) {
            JMenuItem expand = new JMenuItem("Expand");
            JMenuItem collapse = new JMenuItem("Collapse");
            JMenuItem expandAll = new JMenuItem("Expand All");
            JMenuItem collapseAll = new JMenuItem("Collapse All");
            expand.addActionListener(ae -> expandCollapse(tree, tree.getPathForRow(row), true));
            collapse.addActionListener(ae -> expandCollapse(tree, tree.getPathForRow(row), false));
            expandAll.addActionListener(ae -> {
                for (int i = 0; i < tree.getRowCount(); i++) {
                    tree.expandRow(i);
                }
            });
            collapseAll.addActionListener(ae -> {
                for (int i = tree.getRowCount() - 1; i >= 0; i--) {
                    tree.collapseRow(i);
                }
            });

            add(expand);
            add(collapse);
            add(new JSeparator());
            add(expandAll);
            add(collapseAll);
        }

        public void setRow(int row) {
            this.row = row;
        }

        private void expandCollapse(JTree tree, TreePath path, boolean expand) {
            TreeNode node = (TreeNode) path.getLastPathComponent();

            if (node.getChildCount() >= 0) {
                Enumeration enumeration = node.children();
                while (enumeration.hasMoreElements()) {
                    TreeNode n = (TreeNode) enumeration.nextElement();
                    TreePath p = path.pathByAddingChild(n);

                    expandCollapse(tree, p, expand);
                }
            }

            if (expand) {
                tree.expandPath(path);
            } else {
                tree.collapsePath(path);
            }
        }
    }
}