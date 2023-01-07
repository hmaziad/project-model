package org.intellij.sdk.project.model.services;

import java.util.HashMap;

import org.intellij.sdk.project.model.xnodes.DebugNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.util.xmlb.Converter;

public class DebugNodeConverter extends Converter<HashMap<String, DebugNode>> {

    @Override
    public @Nullable HashMap<String, DebugNode> fromString(@NotNull String value) {
        HashMap<String, DebugNode> persistedNodes = new Gson().fromJson(value, new TypeToken<HashMap<String, DebugNode>>() {
        }.getType());
        HashMap<String, DebugNode> correctedNodes = new HashMap<>();
        for (var entry : persistedNodes.entrySet()) {
            correctedNodes.put(entry.getKey(), new DebugNode(entry.getValue()));
        }
        return correctedNodes;
    }

    @Override
    public @Nullable String toString(@NotNull HashMap<String, DebugNode> value) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(value);
    }

}
