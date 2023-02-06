package org.intellij.sdk.project.model.tree.components;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class DebugNodeContainer {
    @Expose
    private LocalDateTime timestamp;
    @Expose
    private String description;
    @Expose
    private String packageName;
    @Expose
    private int lineNumber;
    @Expose
    private final DebugNode node;
}
