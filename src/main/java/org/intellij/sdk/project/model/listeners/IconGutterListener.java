package org.intellij.sdk.project.model.listeners;

import static org.intellij.sdk.project.model.util.HelperUtil.getPackageNameFromVfs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.components.handlers.ReachServices;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;

public class IconGutterListener implements FileEditorManagerListener, ReachServices {

    @Override
    public void selectionChanged(FileEditorManagerEvent event) {
        Optional<String> optionalFileName = getPackageNameFromVfs(event.getNewFile(), event.getManager().getProject());
        if (optionalFileName.isEmpty()) {
            return;
        }
        Collection<Integer> lineNumbers = COMPONENT_SERVICE //
            .getNodeHandler() //
            .getAllContainersPerNames() //
            .values() //
            .stream() //
            .filter(container -> container.getPackageName().equals(optionalFileName.get())) //
            .map(DebugNodeContainer::getLineNumber) //
            .collect(Collectors.toSet());

        MarkupModel markupModel = event.getManager().getSelectedTextEditor().getMarkupModel();

        Arrays //
            .stream(markupModel.getAllHighlighters()) //
            .filter(highlighter -> highlighter.getGutterIconRenderer() instanceof DebugGutterIconRenderer) //
            .forEach(markupModel::removeHighlighter); //

        for (Integer lineNumber : lineNumbers) {
            DebugGutterIconRenderer renderer = new DebugGutterIconRenderer(lineNumber);
            RangeHighlighter rangeHighlighter = markupModel.addLineHighlighter(lineNumber, HighlighterLayer.FIRST, null);
            rangeHighlighter.setGutterIconRenderer(renderer);
        }
    }

}
