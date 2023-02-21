package org.armadillo.core.tree.components;

import com.google.gson.annotations.Expose;

import lombok.Value;

@Value
public class DebugFrame {
    @Expose
    String methodName;
    @Expose
    int lineNumber;
    @Expose
    String packageWithName;
}
