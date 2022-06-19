// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.project.model;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class MyToolWindow {

    private JPanel myToolWindowContent;
    private JTree myTreeActual;

    public MyToolWindow() {
        DefaultMutableTreeNode rootActual = new DefaultMutableTreeNode("Helo");
        rootActual.add(new DefaultMutableTreeNode("1"));
        rootActual.add(new DefaultMutableTreeNode("2"));
        DefaultTreeModel modelActual = (DefaultTreeModel) this.myTreeActual.getModel();
        modelActual.setRoot(rootActual);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }
}
