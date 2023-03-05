package org.armadillo.core.components.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.intellij.openapi.project.Project;

public class SelectionHandler {
    private final Map<Project, LastSelected> lastSelectedMap = new HashMap<>();

    private LastSelected getLastSelection(Project project) {
        return this.lastSelectedMap.computeIfAbsent(project, k -> new LastSelected());
    }

    public Optional<Integer> getLastSelected(Side side, Project project) {
        LastSelected lastSelection = getLastSelection(project);
        Integer selection;
        if (side == Side.LEFT) {
            selection = lastSelection.getLeftLastSelected();
        } else {
            selection = lastSelection.getRightLastSelected();
        }
        return Optional.ofNullable(selection);
    }

    public void setLastSelected(Integer value, Side side, Project project) {
        LastSelected lastSelection = getLastSelection(project);
        if (side == Side.LEFT) {
            lastSelection.setLeftLastSelected(value);
        } else {
            lastSelection.setRightLastSelected(value);
        }
    }
}
