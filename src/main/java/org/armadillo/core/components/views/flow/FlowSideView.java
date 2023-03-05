package org.armadillo.core.components.views.flow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultTreeModel;

import org.armadillo.core.components.handlers.ReachServices;
import org.armadillo.core.components.views.settings.DebugFrameCellRenderer;
import org.armadillo.core.tree.components.DebugFrame;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.armadillo.core.tree.components.DebugTreeManager;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import static javax.swing.JSplitPane.VERTICAL_SPLIT;
import static org.armadillo.core.util.HelperUtil.addStyledText;

public class FlowSideView implements ReachServices {
    private static final Color DIVIDER_COLOR = JBUI.CurrentTheme.DefaultTabs.background();
    private static final int DIVIDER_SIZE = 12;
    private static final double FRAME_BLOCK_SPLIT = 0.25;
    private static final double TREE_BOTTOM_SPLIT = 0.5;
    private DebugTreeManager treeManager;
    private JToggleButton diffToggleButton;
    private JToggleButton framesToggleButton;
    private JToggleButton blockToggleButton;
    private Runnable doDiff;
    private List<DebugNodeContainer> currentNodes;
    private List<Pair<String, List<DebugNodeContainer>>> NODE_FLOW_MAP;
    private int currentSliderIndex = 0;
    private JList<DebugFrame> framesList = new JBList<>();
    private JTextPane textPane = new JTextPane();
    private boolean showBottomPanel = true;
    private JSlider slider;

    public FlowSideView(DebugTreeManager treeManager, JToggleButton diffToggleButton, JToggleButton framesToggleButton, JToggleButton blockToggleButton, Runnable doDiff) {
        this.treeManager = treeManager;
        this.diffToggleButton = diffToggleButton;
        this.framesToggleButton = framesToggleButton;
        this.blockToggleButton = blockToggleButton;
        this.textPane.setEditable(false);
        this.slider = getJSlider();
        this.doDiff = doDiff;
    }

    protected JComponent createMainPanel() {
        // define and add components
        JPanel mainPanel = new JPanel(new BorderLayout());
        JComboBox<String> comboBox = new ComboBox<>();
        JSplitPane contentPanel = getContentPanel();
        JLabel nameAsTitle = new JLabel();
        JPanel northPanel = getNorthPanel(comboBox, slider, nameAsTitle);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        this.NODE_FLOW_MAP = nodeHandler.getSortedFlowContainers();
        // retrieve nodes
        List<String> flowNames = this.NODE_FLOW_MAP.stream().map(pair -> pair.getFirst()).collect(Collectors.toList());
        String firstFlowName = flowNames.get(0);

        flowNames.forEach(comboBox::addItem);
        comboBox.setSelectedItem(firstFlowName);

        updateNodeContainersPerFlowId(slider, firstFlowName);
        updateTreeNodeAndTitle(nameAsTitle);

        comboBox.addActionListener(e -> {
            updateNodeContainersPerFlowId(slider, (String) comboBox.getSelectedItem());
            slider.requestFocus();
        });
        slider.addChangeListener(e -> {
            this.currentSliderIndex = slider.getValue();
            updateTreeNodeAndTitle(nameAsTitle);
            diffNodes();
        });

        // set swing properties
        setMainPanelProperties(mainPanel);
        setSplitPaneProperties(contentPanel, TREE_BOTTOM_SPLIT);
        slider.requestFocus();
        return mainPanel;
    }

    private void diffNodes() {
        if (this.diffToggleButton.isSelected()) {
            this.doDiff.run();
        }
    }

    private void showOtherSplitPanel(JPanel panelToShow, boolean isSelected, JSplitPane splitPane, double ratio, double maxRatio) {
        SwingUtilities.invokeLater(() -> {
            panelToShow.setVisible(isSelected);
            splitPane.setDividerSize(isSelected && ratio == FRAME_BLOCK_SPLIT ? 12 : 0);
            splitPane.setResizeWeight(isSelected ? ratio : maxRatio);
            splitPane.setDividerLocation(isSelected ? ratio : maxRatio);
        });
    }

    private JPanel getTextBlockPanel() {
        JPanel textBlockPanel = new JPanel();
        textBlockPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setViewportView(this.textPane);
        textBlockPanel.add(scrollPane, BorderLayout.CENTER);
        return textBlockPanel;
    }


    private void setSplitPaneProperties(JSplitPane splitPane, double splitRatio) {
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
        SwingUtilities.invokeLater(() -> {
            splitPane.setDividerLocation(splitRatio);
            splitPane.setResizeWeight(splitRatio);
        });
    }

    @NotNull
    private void updateNodeContainersPerFlowId(JSlider slider,String firstFlowName) {
        this.currentNodes =  this.NODE_FLOW_MAP
            .stream()
            .filter(pair -> pair.getFirst().equals(firstFlowName))
            .findFirst()
            .orElseThrow()
            .getSecond()
            .stream()
            .sorted(nodeHandler.getDebugContainerNodeDateComparator())
            .collect(Collectors.toList());
        int maxSize = this.currentNodes.size();
        slider.setLabelTable(toHashTable(maxSize));
        slider.setMaximum(maxSize - 1);
    }

    private void setMainPanelProperties(JPanel mainPanel) {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        mainPanel.setSize(screenDimension.width, screenDimension.height);
    }

    private void updateTreeNodeAndTitle(JLabel nameAsTitle) {
        DebugNodeContainer currentContainer = getCurrentContainer();
        String currentName = currentContainer.getName();
        nameAsTitle.setText(currentName);
        ((DefaultTreeModel) this.treeManager.getDebugTree().getModel()).setRoot(currentContainer.getNode());
        updateFramesBlockToCurrent(currentContainer);
    }

    public DebugNodeContainer getCurrentContainer() {
        return this.currentNodes.get(this.currentSliderIndex);
    }

    private JSplitPane getContentPanel() {
        JScrollPane treePane = new JBScrollPane();
        treePane.setViewportView(this.treeManager.getDebugTree());

        JPanel framesPanel = getFramesPanel();
        JPanel textBlockPanel = getTextBlockPanel();
        JSplitPane framesTextBlockPanel = new JSplitPane(HORIZONTAL_SPLIT, framesPanel, textBlockPanel);
        JSplitPane treeTopFramesBottomPanel = new JSplitPane(VERTICAL_SPLIT, treePane, framesTextBlockPanel);
        treeTopFramesBottomPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

        this.blockToggleButton.addActionListener(
            e -> {
                showOtherSplitPanel(textBlockPanel, this.blockToggleButton.isSelected(), framesTextBlockPanel,
                    this.framesToggleButton.isSelected() ? FRAME_BLOCK_SPLIT : 0, 1);
                showHideBottomPanel(treeTopFramesBottomPanel);
            });

        this.framesToggleButton.addActionListener(
            e -> {
                showOtherSplitPanel(framesPanel, this.framesToggleButton.isSelected(), framesTextBlockPanel,
                    this.blockToggleButton.isSelected() ? FRAME_BLOCK_SPLIT : 1, 0);
                showHideBottomPanel(treeTopFramesBottomPanel);
            });

        setSplitPaneProperties(framesTextBlockPanel, FRAME_BLOCK_SPLIT);
        return treeTopFramesBottomPanel;
    }

    private void showHideBottomPanel(JSplitPane treeTopFramesBottomPanel) {
        boolean newShowPanel = this.blockToggleButton.isSelected() || this.framesToggleButton.isSelected();
        if (newShowPanel != this.showBottomPanel) {
            this.showBottomPanel = newShowPanel;
            SwingUtilities.invokeLater(() -> {
                treeTopFramesBottomPanel.setDividerSize(this.showBottomPanel ? 12 : 0);
                treeTopFramesBottomPanel.setResizeWeight(this.showBottomPanel ? TREE_BOTTOM_SPLIT : 1);
                treeTopFramesBottomPanel.setDividerLocation(this.showBottomPanel ? TREE_BOTTOM_SPLIT : 1);
            });
        }
    }

    private JPanel getFramesPanel() {
        JPanel framesPanel = new JPanel();
        framesPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JBScrollPane();
        this.framesList.setCellRenderer(new DebugFrameCellRenderer());
        scrollPane.setViewportView(this.framesList);
        framesPanel.add(scrollPane, BorderLayout.CENTER);
        return framesPanel;
    }

    private void updateFramesBlockToCurrent(DebugNodeContainer currentContainer) {
        DefaultListModel<DebugFrame> framesListModel = new DefaultListModel<>();
        List<DebugFrame> frames = currentContainer.getFrames();
        if (Objects.nonNull(frames)) {
            framesListModel.addAll(frames);
        }
        this.framesList.setModel(framesListModel);
        this.textPane.setText("");
        addStyledText(this.textPane, currentContainer);
    }

    private JSlider getJSlider() {
        JSlider b = new JSlider(0, 0, 0);
        b.setMajorTickSpacing(1);
        b.setSnapToTicks(true);
        b.setPaintLabels(true);
        b.setUI(new MetalSnapSliderUI());
        b.setBorder(new EmptyBorder(0, 5, 0, 5));
        return b;
    }

    private Hashtable<Integer, JLabel> toHashTable(int nodeSize) {
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 0; i < nodeSize; i++) {
            labels.put(i, new JLabel(String.valueOf(i)));
        }
        return labels;
    }

    private JPanel getNorthPanel(JComboBox<String> comboBox, JSlider slider, JLabel nameAsTitle) {
        JPanel northPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        comboBox.setBorder(new EmptyBorder(7, 7, 7, 7));
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(comboBox, 0);
        nameAsTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(nameAsTitle, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        northPanel.add(panel, gbc);
        gbc.gridy = 1;
        gbc.weightx = 1;
        northPanel.add(slider, gbc);
        return northPanel;
    }

    public JSlider getSlider() {
        return this.slider;
    }
}
