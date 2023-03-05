package org.armadillo.core.constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;

public class MessageDialogues {
    public static boolean getYesNoMessageDialogue(String message, String title, Project project) {
        return Messages.showOkCancelDialog(project, message, title, "Yes", "No", null, null) == Messages.OK;
    }

    public static void showInfoMessageDialogue(String message, String title) {
        Messages.showInfoMessage(message, title);
    }

    public static void getErrorMessageDialogue(String message, Project project) {
        Messages.showMessageDialog(project, message, "Error", Messages.getErrorIcon());
    }

    public static @Nullable @NlsSafe String getRenameDialogue(Project project, String newNodeName, boolean showComment) {
        String dialogueDescription = String.format("Enter new name for session \"%s\"", newNodeName);
        String dialogueErrorMessage = showComment ? String.format("\"%s\" already exists", newNodeName) : null;
        return Messages.showInputDialog(project, dialogueDescription, "Armadillo: Rename Session", null, newNodeName, new CustomInputValidator(), null, dialogueErrorMessage);
    }

    public static @Nullable @NlsSafe String getDescriptionDialogue(Project project, String nodeNames, String currentDescription) {
        String dialogueDescription = String.format("Enter description for saved session(s): \n%s\n\n", nodeNames);
        return Messages.showInputDialog(project, dialogueDescription, "Armadillo: Add Description", null, currentDescription, null, null, null);
    }


    private static class CustomInputValidator implements InputValidator {
        private static final Pattern pattern = Pattern.compile("[~!@#$%^&*()+{}\\[\\]:;,<>/?]");

        @Override
        public boolean checkInput(@NlsSafe String inputString) {
            final Matcher matcher = pattern.matcher(inputString);
            return !matcher.find();
        }

        @Override
        public boolean canClose(@NlsSafe String inputString) {
            return checkInput(inputString);
        }
    }
}
