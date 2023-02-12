package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODE;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODES;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SESSION;
import static org.intellij.sdk.project.model.constants.TextConstants.EXPORT_SESSION;
import static org.intellij.sdk.project.model.constants.TextConstants.GENERATED_SESSION_NAME;
import static org.intellij.sdk.project.model.constants.TextConstants.NODE_DATE_FORMAT;
import static org.intellij.sdk.project.model.util.HelperUtil.getPackageNameForCurrentVfs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.listeners.DebugGutterIconRenderer;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;

public class NodeHandler implements ReachServices {

    public void save(DebugNodeContainer container, Project project) {
        LocalDateTime timestamp = LocalDateTime.now();
        String generatedName = getGenerateName(project, timestamp);
        container.setTimestamp(timestamp);
        save(generatedName, container);
        addGutterIconIfNotExisting(container.getLineNumber(), project);
    }

    private void save(String generatedName, DebugNodeContainer nodeContainer) {
        COMPONENT_SERVICE.setNodeNameInWindow(generatedName);
        PERSISTENCY_SERVICE.getContainers().put(generatedName, nodeContainer);
    }

    private String getGenerateName(Project project, LocalDateTime timestamp) {
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern(NODE_DATE_FORMAT));
        XSourcePosition xSourcePosition = XDebuggerManager.getInstance(project).getCurrentSession().getCurrentPosition();
        String classPart = xSourcePosition.getFile().getNameWithoutExtension();
        return String.format(GENERATED_SESSION_NAME, classPart, formattedTimestamp);
    }

    public boolean delete(List<String> nodeNames, Project project) {
        String sessionsMessage = getNodesMessage(nodeNames);
        String message = String.format(DELETE_SAVED_NODE, sessionsMessage);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, DELETE_SESSION, project);
        if (isSure) {
            nodeNames.forEach(PERSISTENCY_SERVICE.getContainers()::remove);
            clearNonExistingGutterIconsForCurrentFile(project);
            COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
            COMPONENT_SERVICE.setNodeNameInWindow(null);
        }
        return isSure;
    }

    private void clearNonExistingGutterIconsForCurrentFile(Project project) {
        Optional<String> optionalFileName = getPackageNameForCurrentVfs(project);
        if (optionalFileName.isPresent()) {
            Set<Integer> existingLineNumbersForCurrentFile = getAllContainersPerNames() //
                .values() //
                .stream() //
                .filter(container -> container.getPackageName().equals(optionalFileName.get())) //
                .map(DebugNodeContainer::getLineNumber) //
                .collect(Collectors.toSet());

            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (Objects.nonNull(selectedTextEditor)) {
                MarkupModel markupModel = selectedTextEditor.getMarkupModel();
                Arrays.stream(markupModel.getAllHighlighters()) //
                    .filter(highlighter -> highlighter.getGutterIconRenderer() instanceof DebugGutterIconRenderer) //
                    .filter(highlighter -> !existingLineNumbersForCurrentFile.contains(((DebugGutterIconRenderer) highlighter.getGutterIconRenderer()).getLineNumber())) //
                    .forEach(markupModel::removeHighlighter);
            }
        }
    }

    private void addGutterIconIfNotExisting(int lineNumber, Project project) {
        Optional<String> optionalFileName = getPackageNameForCurrentVfs(project);
        if (optionalFileName.isPresent()) {
            Set<Integer> existingLineNumbersForCurrentFile = getAllContainersPerNames() //
                .values() //
                .stream() //
                .filter(container -> container.getPackageName().equals(optionalFileName.get())) //
                .map(DebugNodeContainer::getLineNumber) //
                .collect(Collectors.toSet());

            if (!existingLineNumbersForCurrentFile.contains(lineNumber)) {
                Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if (Objects.nonNull(selectedTextEditor)) {
                    MarkupModel markupModel = selectedTextEditor.getMarkupModel();
                    DebugGutterIconRenderer renderer = new DebugGutterIconRenderer(lineNumber);
                    RangeHighlighter rangeHighlighter = markupModel.addLineHighlighter(lineNumber, HighlighterLayer.FIRST, null);
                    rangeHighlighter.setGutterIconRenderer(renderer);
                }
            }
        }
    }

    @NotNull
    private String getNodesMessage(Collection<String> nodeNames) {
        return nodeNames //
            .stream() //
            .map(nodeName -> String.format("%n - %s", nodeName)) //
            .collect(Collectors.joining());
    }

    public boolean deleteAll(Project project) {
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(DELETE_SAVED_NODES, DELETE_SESSION, project);
        if (isSure) {
            PERSISTENCY_SERVICE.getContainers().clear();
        }
        return isSure;
    }

    public Optional<DebugNodeContainer> getNodeContainerByName(String nodeName) {
        return Optional.ofNullable(PERSISTENCY_SERVICE.getContainers().get(nodeName));
    }

    public List<String> getSortedNodeNames(Integer lineNumber) {
        return PERSISTENCY_SERVICE //
            .getContainers() //
            .entrySet() //
            .stream() //
            .sorted(getDebugContainerDateComparator())//
            .filter(entry -> entry.getValue().getLineNumber() == lineNumber) //
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
    }

    public List<String> getSortedNodeNames() {
        return PERSISTENCY_SERVICE //
            .getContainers() //
            .entrySet() //
            .stream() //
            .sorted(getDebugContainerDateComparator())//
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
    }

    private Comparator<Map.Entry<String, DebugNodeContainer>> getDebugContainerDateComparator() {
        return Comparator.comparing(e1 -> {
            LocalDateTime timestamp = e1.getValue().getTimestamp();
            return timestamp == null ? LocalDateTime.now() : timestamp;
        }, Comparator.reverseOrder());
    }

    public Map<String, DebugNodeContainer> getAllContainersPerNames() {
        return PERSISTENCY_SERVICE.getContainers();
    }

    public void renameNode(String from, String to) {
        Map<String, DebugNodeContainer> containers = PERSISTENCY_SERVICE.getContainers();
        DebugNodeContainer fromContainer = containers.get(from);
        containers.remove(from);
        containers.put(to, fromContainer);
    }

    public void export(@NotNull List<String> selectedKeys, Project project) {
        Path pathWithDir = createDirectoryIfNotExists(project.getBasePath());
        Map<String, File> filesToExport = new HashMap<>();
        Set<String> unExported = new HashSet<>();
        for (String selectedKey : selectedKeys) {
            String fileName = selectedKey.replace(":", "-") + ".json";
            String fullPath = String.format("%s%s", pathWithDir.toUri().getPath(), fileName);
            File nodeAsFile = new File(fullPath);
            if (nodeAsFile.exists()) {
                unExported.add(selectedKey);
            } else {
                filesToExport.put(selectedKey, nodeAsFile);
            }
        }

        if (!unExported.isEmpty()) {
            String errorMessage = String.format("Please rename the sessions below because they already exists under directory:%n%s%n%s", pathWithDir, getNodesMessage(unExported));
            MessageDialogues.getErrorMessageDialogue(errorMessage, project);
            return;
        }
        boolean isSure =
            MessageDialogues.getYesNoMessageDialogue(String.format("Are you sure you want to export sessions: %n%s", getNodesMessage(selectedKeys)), EXPORT_SESSION, project);
        if (!isSure) {
            return;
        }

        try {
            for (var entry : filesToExport.entrySet()) {
                String nodeName = entry.getKey();
                File nodeAsFile = entry.getValue();
                DebugNodeContainer nodeContainer = getNodeContainerByName(nodeName).orElseThrow();
                HashMap<String, DebugNodeContainer> namePerNode = new HashMap<>();
                namePerNode.put("root", nodeContainer);
                String nodeAsJson = COMPONENT_SERVICE.getNodeConverter().toString(namePerNode);
                FileWriter myWriter = new FileWriter(nodeAsFile);
                myWriter.write(Objects.requireNonNull(nodeAsJson, "Session contains no data"));
                myWriter.close();
                nodeAsFile.createNewFile();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not export session", e);
        }
        MessageDialogues
            .showInfoMessageDialogue(String.format("Successfully exported the following file(s): %n%s%n%n under path: %s%n ", getNodesMessage(selectedKeys), pathWithDir),
                "Export Successful");
    }

    private Path createDirectoryIfNotExists(String path) {
        String dirName = "/armadillo";
        try {
            return Files.createDirectories(Paths.get(path + dirName));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not create %s dir", dirName), e);
        }
    }

    public void doImport(Project project) {
        FileChooserDescriptor jsonOnly = FileChooserDescriptorFactory.createSingleFileDescriptor(JsonFileType.INSTANCE);
        VirtualFile chosenFile = FileChooser.chooseFile(jsonOnly, project, null);
        if (Objects.nonNull(chosenFile)) {
            String errorMessage = chosenFile.getName() + " file is empty";
            Map<String, DebugNodeContainer> nodes = PERSISTENCY_SERVICE.getContainers();
            String fileName = Objects.requireNonNull(chosenFile.getNameWithoutExtension(), "File is not valid");
            if (nodes.containsKey(fileName)) {
                MessageDialogues.getErrorMessageDialogue(String.format("A session with name \"%s\" already exists", fileName), project);
            } else {
                CharSequence charsSequence = Objects.requireNonNull(FileDocumentManager.getInstance().getDocument(chosenFile), errorMessage).getCharsSequence();
                String content = String.valueOf(charsSequence);
                HashMap<String, DebugNodeContainer> nodeFromJson = COMPONENT_SERVICE.getNodeConverter().fromString(content);
                DebugNodeContainer nodeContainer = Objects.requireNonNull(nodeFromJson, errorMessage).entrySet().iterator().next().getValue();
                save(fileName, nodeContainer);
            }
        }
    }

    public DebugNodeContainer getCurrentSession(Project project) {
        return COMPONENT_SERVICE //
            .getSnapHandler() //
            .getCurrentSession(project) //
            .orElseThrow(() -> new IllegalStateException("Why you messing?"));
    }

}
