package org.intellij.sdk.project.model.util;

import java.util.Optional;

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
}
