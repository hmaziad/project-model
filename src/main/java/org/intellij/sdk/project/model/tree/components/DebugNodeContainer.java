package org.intellij.sdk.project.model.tree.components;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DebugNodeContainer {
    @Expose
    private final LocalDateTime timestamp;
    @Expose
    private final DebugNode node;
}
