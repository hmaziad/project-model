package org.armadillo.core.util;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.armadillo.core.tree.components.DebugNode;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.util.xmlb.Converter;

public class DebugContainerConverter extends Converter<HashMap<String, DebugNodeContainer>> {
    private final Gson gson = new GsonBuilder() //
        .excludeFieldsWithoutExposeAnnotation() //
        .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime())//
        .create();

    @Override
    public @Nullable HashMap<String, DebugNodeContainer> fromString(@NotNull String value) {
        HashMap<String, DebugNodeContainer> persistedNodes = this.gson.fromJson(value, new TypeToken<HashMap<String, DebugNodeContainer>>() {}.getType());
        HashMap<String, DebugNodeContainer> correctedNodes = new HashMap<>();
        for (var entry : persistedNodes.entrySet()) {
            DebugNodeContainer entryValue = entry.getValue();
            DebugNodeContainer container = DebugNodeContainer //
                .builder() //
                .name(entryValue.getName()) //
                .timestamp(entryValue.getTimestamp()) //
                .flowTimestamp(entryValue.getFlowTimestamp()) //
                .description(entryValue.getDescription()) //
                .lineNumber(entryValue.getLineNumber()) //
                .lineIndexDebugged(entryValue.getLineIndexDebugged()) //
                .packageName(entryValue.getPackageName()) //
                .flowId(entryValue.getFlowId()) //
                .textBlock(entryValue.getTextBlock()) //
                .frames(entryValue.getFrames()) //
                .node(new DebugNode(entry.getValue().getNode())) //
                .build();

            correctedNodes.put(entry.getKey(), container);
        }
        return correctedNodes;
    }

    @Override
    public @Nullable String toString(@NotNull HashMap<String, DebugNodeContainer> value) {
        return this.gson.toJson(value);
    }

}
