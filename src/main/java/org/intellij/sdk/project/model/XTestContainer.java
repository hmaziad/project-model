// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.intellij.sdk.project.model;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink;

public class XTestContainer<T> {
    private String myErrorMessage;

    public void tooManyChildren(int remaining) {
    }

    public void setMessage(@NotNull String message, Icon icon, @NotNull final SimpleTextAttributes attributes, @Nullable XDebuggerTreeNodeHyperlink link) {
    }

    public void setErrorMessage(@NotNull String message, @Nullable XDebuggerTreeNodeHyperlink link) {
        setErrorMessage(message);
    }

    public void setErrorMessage(@NotNull String errorMessage) {
        myErrorMessage = errorMessage;
    }
}
