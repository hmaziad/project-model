package org.intellij.sdk.project.model.components.views.settings;

import static org.intellij.sdk.project.model.constants.TextConstants.SETTINGS_VIEW_TITLE;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugFrame;
import org.intellij.sdk.project.model.tree.components.DebugTreeManager;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

public class SettingsView extends DialogWrapper implements ReachServices {
    private static final EmptyBorder EMPTY_BORDER = JBUI.Borders.empty();
    private static final Color BORDER_COLOR = JBUI.CurrentTheme.ToolWindow.borderColor();
    private static final Color DIVIDER_COLOR = JBUI.CurrentTheme.DefaultTabs.background();
    private static final BorderUIResource.LineBorderUIResource BORDER_UI_RESOURCE = new BorderUIResource.LineBorderUIResource(BORDER_COLOR);
    private static final int DIVIDER_SIZE = 12;
    private static final double KEYS_FRAMES_SPLIT_RATIO = 0.5;
    private static final double LEFT_RIGHT_SPLIT_RATIO = 0.3;
    private final DebugTreeManager debugTreeManager = new DebugTreeManager(false);
    private final JLabel timestamp = new JLabel();
    private final Project project;
    private final JLabel description = new JLabel();
    private JBList<String> keysList;
    private Integer lineNumber;
    private JBList<DebugFrame> framesList = new JBList<>();

    public SettingsView(Project project) {
        super(true);
        setTitle(SETTINGS_VIEW_TITLE);
        this.project = project;
        init();
    }

    public SettingsView(Project project, Integer lineNumber) {
        this(project);
        this.lineNumber = lineNumber;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // define panels
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JSplitPane keysFrameSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getKeysPanel(), getFramesPanel());
        JSplitPane leftRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keysFrameSplit, getTreePanel());

        // add listeners
        KeySelectionListener keySelectionListener = new KeySelectionListener(this.keysList, this.description, this.timestamp, this.debugTreeManager, this.framesList);
        KeyMouseListener keyMouseListener = new KeyMouseListener(this.keysList, this.project, this.description, this.lineNumber);
        this.keysList.addListSelectionListener(keySelectionListener);
        this.keysList.addMouseListener(keyMouseListener);
        new ListSpeedSearch<>(this.keysList);

        // define properties
        setSplitPaneProperties(keysFrameSplit, KEYS_FRAMES_SPLIT_RATIO);
        setSplitPaneProperties(leftRightSplit, LEFT_RIGHT_SPLIT_RATIO);
        setDialogueProperties(dialogPanel, leftRightSplit);

        //select first key
        if (this.keysList.getItemsCount() > 0) {
            this.keysList.setSelectedIndex(0);
        }
        return dialogPanel;
    }

    private void setDialogueProperties(JPanel dialogPanel, JSplitPane leftRightSplit) {
        dialogPanel.setSize(1100, 800);
        setSize(1100, 800);

        dialogPanel.add(this.timestamp, BorderLayout.NORTH);
        dialogPanel.add(leftRightSplit, BorderLayout.CENTER);
        dialogPanel.add(this.description, BorderLayout.SOUTH);
    }

    private void setSplitPaneProperties(JSplitPane splitPane, double splitRatio) {
        setSplitPaneProperties(splitPane);
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(splitRatio);
            splitPane.setResizeWeight(splitRatio);
        });
    }

    private void setSplitPaneProperties(JSplitPane splitPane) {
        splitPane.setDividerSize(DIVIDER_SIZE);
        splitPane.setContinuousLayout(true);
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {

                    public void setBorder(Border b) {
                        // do nothing
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
        JScrollPane scrollPane = new JBScrollPane();

        this.framesList.setCellRenderer(new DebugFrameCellRenderer());

        scrollPane.setViewportView(this.framesList);
        framesPanel.setBorder(BORDER_UI_RESOURCE);
        scrollPane.setBorder(EMPTY_BORDER);
        framesPanel.add(scrollPane, BorderLayout.CENTER);
        return framesPanel;
    }

    private JPanel getTreePanel() {
        JPanel contentPanel = new JPanel();
        JScrollPane scrollPane = new JBScrollPane();

        scrollPane.setViewportView(this.debugTreeManager.getDebugTree());

        scrollPane.setBorder(EMPTY_BORDER);
        contentPanel.setBorder(BORDER_UI_RESOURCE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        return contentPanel;
    }

    private Component getKeysPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();

        this.keysList = new JBList<>(getKeysList());

        jPanel.setBorder(BORDER_UI_RESOURCE);
        scrollPane.setBorder(EMPTY_BORDER);
        scrollPane.setViewportView(this.keysList);
        jPanel.add(scrollPane, BorderLayout.CENTER);
        return jPanel;
    }

    private ListModel<String> getKeysList() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<String> nodeNames;
        if (Objects.isNull(this.lineNumber)) {
            nodeNames = nodeHandler.getSortedNodeNames();
        } else {
            nodeNames = nodeHandler.getSortedNodeNames(this.lineNumber);
        }
        listModel.addAll(nodeNames);
        return listModel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.keysList;
    }
}