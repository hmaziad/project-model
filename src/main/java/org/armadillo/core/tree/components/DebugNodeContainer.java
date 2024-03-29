package org.armadillo.core.tree.components;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebugNodeContainer {
    @Expose
    private String name;
    @Expose
    private LocalDateTime timestamp;
    @Expose
    private String description;
    @Expose
    private String packageName;
    @Expose
    private int lineNumber;
    @Expose
    private int lineIndexDebugged;
    @Expose
    private String flowId;
    @Expose
    private LocalDateTime flowTimestamp;
    @Expose
    private String textBlock;
    @Expose
    private List<DebugFrame> frames;
    @Expose
    private final DebugNode node;
}
