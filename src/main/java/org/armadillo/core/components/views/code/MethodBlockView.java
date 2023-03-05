package org.armadillo.core.components.views.code;

import static org.armadillo.core.constants.TextConstants.METHOD_BLOCK_VIEW_TITLE;
import static org.armadillo.core.util.HelperUtil.addStyledText;

import java.awt.*;

import javax.swing.*;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.ui.DialogWrapper;

public class MethodBlockView extends DialogWrapper {

    private DebugNodeContainer debugNodeContainer;

    public MethodBlockView(DebugNodeContainer debugNodeContainer) {
        super(true);
        this.debugNodeContainer = debugNodeContainer;
        setTitle(METHOD_BLOCK_VIEW_TITLE);
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JTextPane textField = new JTextPane();
        textField.setEditable(false);
        addStyledText(textField, this.debugNodeContainer);
        mainPanel.add(textField);
        return mainPanel;
    }

}
