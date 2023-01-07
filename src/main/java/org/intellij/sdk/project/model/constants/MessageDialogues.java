package org.intellij.sdk.project.model.constants;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;

public class MessageDialogues {
    public static boolean getYesNoMessageDialogue(String message, String title, Project project) {
        return Messages.showOkCancelDialog(project, message, title, "Yes", "No", null, null) == Messages.OK;
    }

    // todo remove
    public static void getErrorMessageDialogue(String message, Project project) {
        Messages.showMessageDialog(project,message, "Error", Messages.getErrorIcon());
    }

    public static @Nullable @NlsSafe String getRenameDialogue(Project project, String newNodeName) {
        return Messages.showInputDialog(project, "Enter new name for node", "Rename Node", null, null, new CustomInputValidator(), null, newNodeName);
    }

    private static class CustomInputValidator implements InputValidator {
        @Override
        public boolean checkInput(@NlsSafe String inputString) {
            return !inputString.contains(" ");
        }

        @Override
        public boolean canClose(@NlsSafe String inputString) {
            return checkInput(inputString);
        }
    }
}
