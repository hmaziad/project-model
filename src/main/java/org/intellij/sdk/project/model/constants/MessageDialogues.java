package org.intellij.sdk.project.model.constants;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MessageDialogues {
    public static boolean getYesNoMessageDialogue(String message, String title, Project project) {
        return Messages.showOkCancelDialog(project, message, title, "Yes", "No", null, null) == Messages.OK;
    }

    public static void getErrorMessageDialogue(String message, Project project) {
        Messages.showMessageDialog(project,message, "Error", Messages.getErrorIcon());
    }
}
