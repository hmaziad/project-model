package org.armadillo.core.util;

import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.armadillo.core.tree.components.DebugNodeContainer;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiJavaFileImpl;

public class HelperUtil {
    public static Optional<String> getPackageNameFromVfs(VirtualFile virtualFile, Project project) {
        if (Objects.nonNull(virtualFile) && virtualFile.getName().endsWith(".java")) {
            PsiJavaFileImpl fileImpl = (PsiJavaFileImpl) PsiManager.getInstance(project).findFile(virtualFile);
            return Optional.of(fileImpl.getPackageName() + "." + fileImpl.getName());
        }
        return Optional.empty();
    }

    public static Optional<String> getPackageNameForCurrentVfs(Project project) {
        FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor();
        if (Objects.isNull(selectedEditor)) {
            return Optional.empty();
        }
        VirtualFile selectedFile = selectedEditor.getFile();
        if (selectedFile.getName().endsWith(".java")) {
            PsiJavaFileImpl fileImpl = (PsiJavaFileImpl) PsiManager.getInstance(project).findFile(selectedFile);
            return Optional.of(fileImpl.getPackageName() + "." + fileImpl.getName());
        }
        return Optional.empty();
    }

    public static void addStyledText(JTextPane textPane, DebugNodeContainer currentContainer) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setBold(keyWord, true);
        String textBlock = currentContainer.getTextBlock();
        if (Objects.isNull(textBlock)) {
            return;
        }
        String[] strings = textBlock.split("\n");
        try {
            for (int i = 0; i < strings.length; i++) {
                String currentLine = strings[i];
                if (i == currentContainer.getLineIndexDebugged()) {
                    doc.insertString(doc.getLength(), currentLine, keyWord);
                } else {
                    doc.insertString(doc.getLength(), currentLine, null);
                }
                doc.insertString(doc.getLength(), "\n", null);
            }
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}
