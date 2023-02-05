package org.intellij.sdk.project.model.listeners;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;
import org.intellij.sdk.project.model.components.views.SettingsView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;

import icons.SdkIcons;

public class IconGutterListener implements FileEditorManagerListener {
    private final GutterIconRenderer renderer = new GutterIconRenderer() {
        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public @NotNull Icon getIcon() {
            return SdkIcons.VIEW_NODES_ICON;
        }

        @Override
        public @Nullable String getTooltipText() {
            return "Show snaps at this line";
        }

        @Override
        public @Nullable AnAction getClickAction() {
            return new AnAction() {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    DataContext dataContext = e.getDataContext();
                    Editor editor = (Editor) dataContext.getData("host.editor");
                    //editor.logicalPositionToOffset(e.getInputEvent())
                    var mouseevent = (MouseEvent) e.getInputEvent();
                    Point point = new Point(mouseevent.getX(), mouseevent.getY());
                    System.out.println("Line number: " + convertPointToLineNumber(point,editor));
                    new SettingsView(e.getProject()).showAndGet();
                }
            };
        }

    };

    private int convertPointToLineNumber(final Point p, Editor editor) {
        Document document = editor.getDocument();
        int line = EditorUtil.yPositionToLogicalLine(editor, p);
        if (!isValidLine(document, line)) return -1;

        int startOffset = document.getLineStartOffset(line);
        final FoldRegion region = editor.getFoldingModel().getCollapsedRegionAtOffset(startOffset);
        if (region != null) {
            return document.getLineNumber(region.getEndOffset());
        }
        return line;
    }

    private static boolean isValidLine(@NotNull Document document, int line) {
        if (line < 0) return false;
        int lineCount = document.getLineCount();
        return lineCount == 0 ? line == 0 : line < lineCount;
    }

    public void selectionChanged(FileEditorManagerEvent event) {
        Editor selectedEditor = event.getManager().getSelectedTextEditor();
        setupIcons(selectedEditor);
//        GutterIconDescriptor.Option iconDescriptor = new GutterIconDescriptor.Option("id11111", "sssdddd", SdkIcons.SNAP_ICON);
//        var iconLineMarkerProvider = new IconLineMarkerProvider();
//        iconLineMarkerProvider.getOptions();
    }

    private void setupIcons(Editor selectedEditor) {
        int line = 2;
        RangeHighlighter rangeHighlighter = selectedEditor.getMarkupModel().addLineHighlighter(line, HighlighterLayer.FIRST, null);
        rangeHighlighter.setGutterIconRenderer(this.renderer);
    }

}
