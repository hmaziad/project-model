package org.intellij.sdk.project.model.util;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
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
                .timestamp(entryValue.getTimestamp()) //
                .description(entryValue.getDescription()) //
                .lineNumber(entryValue.getLineNumber()) //
                .packageName(entryValue.getPackageName()) //
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
