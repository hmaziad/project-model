package org.armadillo.core.util;

import java.util.Objects;
import java.util.Optional;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiJavaFileImpl;

public class HelperUtil {
    public static Optional<String> getPackageNameFromVfs(VirtualFile virtualFile, Project project) {
        if (virtualFile.getName().endsWith(".java")) {
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
}
