package org.intellij.sdk.project.model.components.views;

import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;

public class ExpandableSettingsView extends DialogWrapper {
    private static final EmptyBorder EMPTY_BORDER = JBUI.Borders.empty();
    private static final Color BORDER_COLOR = JBUI.CurrentTheme.ToolWindow.borderColor();
    private static final Color DIVIDER_COLOR = JBUI.CurrentTheme.DefaultTabs.background();
    private static final BorderUIResource.LineBorderUIResource BORDER_UI_RESOURCE = new BorderUIResource.LineBorderUIResource(BORDER_COLOR);
    private static final int DIVIDER_SIZE = 12;
    private JList<String> jList;
    private static final String LONG_TEXT =
        "<html><xmp>Description<Hellooo> sa;Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32. The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.</xmp></html>";

    public ExpandableSettingsView() {
        super(true); // use current window as parent
        setTitle("Armadillo: Manage Saved Sessions");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JSplitPane keysFrameSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getKeysPanel(), getFramesPanel());
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keysFrameSplit, getTreePanel());

        JLabel description = new JLabel(LONG_TEXT);
        this.jList.addListSelectionListener(e -> onKeySelection(description));
        // set the size of frame
        dialogPanel.setSize(1100, 800);
        setSize(1100, 800);


        invokeLater(keysFrameSplit, mainSplit);
        setSplitPaneProperties(mainSplit);
        setSplitPaneProperties(keysFrameSplit);
        // add components in order
        dialogPanel.add(new JLabel("Timestamp 2013m2130:43:12:34"), BorderLayout.NORTH);
        dialogPanel.add(mainSplit, BorderLayout.CENTER);
        dialogPanel.add(description, BorderLayout.SOUTH);
        return dialogPanel;
    }

    private void invokeLater(JSplitPane keysFrameSplit, JSplitPane mainSplit) {
        SwingUtilities.invokeLater(() -> {
            keysFrameSplit.setDividerLocation(0.5);
            keysFrameSplit.setResizeWeight(0.5);
            mainSplit.setDividerLocation(0.3);
            mainSplit.setResizeWeight(0.3);

        });
    }

    private void onKeySelection(JLabel description) {
        if (this.jList.getSelectedIndex() % 2 == 0) {
            description.setText("");
        } else {
            description.setText(LONG_TEXT);
        }
    }

    public void setSplitPaneProperties(JSplitPane splitPane) {
        splitPane.setDividerSize(DIVIDER_SIZE);
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {

                    public void setBorder(Border b) {
                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(DIVIDER_COLOR);
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        super.paint(g);
                    }
                };
            }
        });
        splitPane.setBorder(null);
    }

    private JPanel getFramesPanel() {
        JPanel framesPanel = new JPanel();
        framesPanel.setLayout(new BorderLayout());
        String[] strings = IntStream.range(1, 30).mapToObj(i -> "d.").collect(Collectors.toList()).toArray(String[]::new);
        JList<String> jList = new JBList<>(strings);
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setViewportView(jList);
        framesPanel.setBorder(BORDER_UI_RESOURCE);
        scrollPane.setBorder(EMPTY_BORDER);
        framesPanel.add(scrollPane, BorderLayout.CENTER);
        return framesPanel;
    }

    private JPanel getTreePanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BORDER_UI_RESOURCE);
        contentPanel.setLayout(new BorderLayout());
        JTree jTree = new Tree();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(new DefaultMutableTreeNode("1"));
        IntStream.range(1, 50).mapToObj(i -> new DefaultMutableTreeNode("Child" + i)).forEach(root::add);
        ((DefaultTreeModel) jTree.getModel()).setRoot(root);
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setViewportView(jTree);
        scrollPane.setBorder(EMPTY_BORDER);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        return contentPanel;
    }

    private Component getKeysPanel() {
        String[] strings = IntStream.range(1, 30)
            .mapToObj(i -> "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ")
            .collect(Collectors.toList()).toArray(String[]::new);
        strings[0] = "Hussein";
        this.jList = new JBList<>(strings);
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setViewportView(this.jList);
        scrollPane.setBorder(EMPTY_BORDER);
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(scrollPane, BorderLayout.CENTER);
        jPanel.setBorder(BORDER_UI_RESOURCE);
        return jPanel;
    }
}