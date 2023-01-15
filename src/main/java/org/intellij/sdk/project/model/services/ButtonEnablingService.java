package org.intellij.sdk.project.model.services;

import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ButtonEnablingService {
    private JButton snapButton;
    private JButton clearButton;

    public void setSnapButtonEnabled(boolean isEnabled) {
        this.snapButton.setEnabled(isEnabled);
    }

    public void setClearButtonEnabled(boolean isEnabled) {
        this.clearButton.setEnabled(isEnabled);
    }
}
