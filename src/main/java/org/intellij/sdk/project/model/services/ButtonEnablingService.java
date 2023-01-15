package org.intellij.sdk.project.model.services;

import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ButtonEnablingService {
    private JButton jButton;

    public void disableButton() {
//        this.jButton.setEnabled(false);
    }

    public void enableButton() {
//        this.jButton.setEnabled(true);
    }
}
