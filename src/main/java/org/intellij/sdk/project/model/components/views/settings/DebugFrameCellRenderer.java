package org.intellij.sdk.project.model.components.views.settings;

import static org.intellij.sdk.project.model.constants.TextConstants.EMPTY_STRING;

import javax.swing.*;
import org.intellij.sdk.project.model.tree.components.DebugFrame;
import org.jetbrains.annotations.NotNull;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

public class DebugFrameCellRenderer extends ColoredListCellRenderer<DebugFrame> {
    @Override
    protected void customizeCellRenderer(@NotNull JList<? extends DebugFrame> list, DebugFrame frame, int index, boolean selected, boolean hasFocus) {
        String packageWithName = frame.getPackageWithName();
        int lastPointIndex = packageWithName.lastIndexOf('.');
        String fileName = packageWithName;
        String packageName = EMPTY_STRING;
        if (lastPointIndex != -1) {
            fileName = packageWithName.substring(lastPointIndex + 1);
            packageName = String.format("(%s)", packageWithName.substring(0, lastPointIndex));
        }

        String firstPart = "%s: %s, %s ";
        append(String.format(firstPart, frame.getMethodName(), frame.getLineNumber(), fileName), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        append(packageName, SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
    }
}
