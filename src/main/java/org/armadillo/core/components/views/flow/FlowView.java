package org.armadillo.core.components.views.flow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.armadillo.core.components.handlers.DiffHandler;
import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.armadillo.core.tree.components.DebugTreeManager;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static org.armadillo.core.constants.TextConstants.FLOW_VIEW_TITLE;

public class FlowView extends DialogWrapper implements ReachServices {
    private static final Color DIVIDER_COLOR = JBUI.CurrentTheme.DefaultTabs.background();
    private final DebugTreeManager mainTreeManager = new DebugTreeManager(true);
    private final DebugTreeManager diffTreeManager = new DebugTreeManager(true);
    private final DiffHandler diffHandler = new DiffHandler();
    private final Project project;
    private FlowSideView mainFlowSide;
    private FlowSideView diffFlowSide;

    public FlowView(Project project) {
        super(true);
        this.project = project;
        setTitle(FLOW_VIEW_TITLE);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JToolBar toolbar = new JToolBar();
        JToggleButton diffToggleButton = new JToggleButton("Diff", true);
        JToggleButton framesToggleButton = new JToggleButton("Frames", true);
        JToggleButton blockToggleButton = new JToggleButton("Block", true);
        // Add some buttons to the toolbar
        toolbar.add(diffToggleButton);
        toolbar.add(framesToggleButton);
        toolbar.add(blockToggleButton);

        toolbar.setBorder(JBUI.Borders.empty());
        diffToggleButton.setBorder(JBUI.Borders.empty());
        framesToggleButton.setBorder(JBUI.Borders.empty());
        blockToggleButton.setBorder(JBUI.Borders.empty());

        this.mainFlowSide = new FlowSideView(this.mainTreeManager, diffToggleButton, framesToggleButton, blockToggleButton, this::doDiff);
        this.diffFlowSide = new FlowSideView(this.diffTreeManager, diffToggleButton, framesToggleButton, blockToggleButton, this::doDiff);

        JComponent mainFlowPanel = this.mainFlowSide.createMainPanel();
        JComponent diffFlowPanel = this.diffFlowSide.createMainPanel();
        JSplitPane splitPane = new JSplitPane(HORIZONTAL_SPLIT, mainFlowPanel, diffFlowPanel);

        diffToggleButton.addActionListener(e -> showDiffView(diffToggleButton.isSelected(), splitPane));

        // set swing properties
        setSplitPaneProperties(splitPane);
        setToolbarProperties(toolbar);
        setCurrentScreenProperties(diffFlowPanel);

        // add components
        mainPanel.add(toolbar, BorderLayout.PAGE_START);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        doDiff();
        return mainPanel;
    }

    private void setCurrentScreenProperties(JComponent diffFlowPanel) {
        diffFlowPanel.setVisible(true);
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenDimension.width, screenDimension.height);
    }

    private void showDiffView(boolean isSelected, JSplitPane splitPane) {
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerSize(isSelected ? 12 : 0);
            splitPane.setResizeWeight(isSelected ? 0.5 : 1);
            splitPane.setDividerLocation(isSelected ? 0.5 : 1);
        });
    }

    private void setToolbarProperties(JToolBar toolbar) {
        toolbar.setFloatable(false); // Optional - to prevent toolbar from being moved by the user
        toolbar.setBackground(JBUI.CurrentTheme.CustomFrameDecorations.paneBackground());
    }

    private void setSplitPaneProperties(JSplitPane splitPane) {
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(12);

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

    private void doDiff() {
        this.mainTreeManager.clearNodeColors();
        this.diffTreeManager.clearNodeColors();
        DebugNodeContainer mainContainer = this.mainFlowSide.getCurrentContainer();
        DebugNodeContainer diffContainer = this.diffFlowSide.getCurrentContainer();

        String mainContainerName = mainContainer.getName();
        String diffContainerName = diffContainer.getName();
        List<List<List<Integer>>> changes =
            this.diffHandler.diffNodes(mainContainer.getNode(), mainContainerName, diffContainer.getNode(),
                diffContainerName, false, this.project);
        List<List<Integer>> additions = changes.get(0);
        List<List<Integer>> deletions = changes.get(1);
        List<List<Integer>> modifications1 = changes.get(2);
        List<List<Integer>> modifications2 = changes.get(3);
        this.mainTreeManager.addDiffDeletions(deletions);
        this.mainTreeManager.addDiffModifications(modifications1);

        this.diffTreeManager.addDiffInsertions(additions);
        this.diffTreeManager.addDiffModifications(modifications2);
        this.mainTreeManager.getDebugTree().repaint();
        this.diffTreeManager.getDebugTree().repaint();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return this.mainFlowSide.getSlider();
    }
}
