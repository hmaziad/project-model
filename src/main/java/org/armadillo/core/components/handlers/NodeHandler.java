package org.armadillo.core.components.handlers;

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

import org.armadillo.core.constants.MessageDialogues;
import org.armadillo.core.constants.TextConstants;
import org.armadillo.core.license.CheckLicense;
import org.armadillo.core.listeners.DebugGutterIconRenderer;
import org.armadillo.core.services.PersistencyService;
import org.armadillo.core.tree.components.DebugNodeContainer;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ServiceManager;
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
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;

import static org.armadillo.core.constants.TextConstants.GET_PAID_VERSION_IMPORT;
import static org.armadillo.core.constants.TextConstants.REGISTER_PLUGIN;
import static org.armadillo.core.util.HelperUtil.getPackageNameForCurrentVfs;

public class NodeHandler implements ReachSomeServices {
    private static final PersistencyService persistencyService = ServiceManager.getService(PersistencyService.class);
    private LocalDateTime flowDate;

    public String save(DebugNodeContainer container, Project project) {
        LocalDateTime timestamp = LocalDateTime.now();
        String generatedName = getGenerateName(project, timestamp);
        container.setName(generatedName);
        container.setTimestamp(timestamp);
        container.setFlowTimestamp(this.flowDate);
        container.setFlowId(treeHandler.getFlowId(project));
        save(generatedName, container);
        addGutterIconIfNotExisting(container.getLineNumber(), project);
        return generatedName;
    }

    private void save(String generatedName, DebugNodeContainer nodeContainer) {
        persistencyService.getContainers().put(generatedName, nodeContainer);
    }

    public List<Pair<String, List<DebugNodeContainer>>> getSortedFlowContainers() {
        return getAllContainersPerNames()
            .values()
            .stream()
            .collect(Collectors.groupingBy(DebugNodeContainer::getFlowId, Collectors.toList()))
            .entrySet()
            .stream()
            .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(e -> e.getSecond().get(0).getFlowTimestamp(), Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    private String getGenerateName(Project project, LocalDateTime timestamp) {
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern(TextConstants.NODE_DATE_FORMAT));
        XSourcePosition xSourcePosition = XDebuggerManager.getInstance(project).getCurrentSession().getCurrentPosition();
        String classPart = xSourcePosition.getFile().getNameWithoutExtension();
        return String.format(TextConstants.GENERATED_SESSION_NAME, classPart, formattedTimestamp);
    }

    public boolean delete(List<String> nodeNames, Project project) {
        String sessionsMessage = getNodesMessage(nodeNames);
        String message = String.format(TextConstants.DELETE_SAVED_NODE, sessionsMessage);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, TextConstants.DELETE_SESSION, project);
        if (isSure) {
            nodeNames.forEach(persistencyService.getContainers()::remove);
            clearNonExistingGutterIconsForCurrentFile(project);
            treeHandler.getDebugTreeManager(project).setRoot(null);
        }
        return isSure;
    }

    public void quickDelete(String nodeName, Project project) {
        persistencyService.getContainers().remove(nodeName);
        clearNonExistingGutterIconsForCurrentFile(project);
        treeHandler.getDebugTreeManager(project).setRoot(null);
    }

    private void clearNonExistingGutterIconsForCurrentFile(Project project) {
        Optional<String> optionalFileName = getPackageNameForCurrentVfs(project);
        if (optionalFileName.isPresent()) {
            Set<Integer> existingLineNumbersForCurrentFile = getAllContainersPerNames() //
                .values() //
                .stream() //
                .filter(container -> Objects.equals(container.getPackageName(), optionalFileName.get())) //
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
        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (Objects.nonNull(selectedTextEditor)) {
            MarkupModel markupModel = selectedTextEditor.getMarkupModel();
            boolean doesExist = Arrays //
                .stream(markupModel.getAllHighlighters()) //
                .filter(highlighter -> highlighter.getGutterIconRenderer() instanceof DebugGutterIconRenderer) //
                .anyMatch(highlighter -> lineNumber == (((DebugGutterIconRenderer) highlighter.getGutterIconRenderer()).getLineNumber()));
            if (!doesExist) {
                DebugGutterIconRenderer renderer = new DebugGutterIconRenderer(lineNumber);
                RangeHighlighter rangeHighlighter = markupModel.addLineHighlighter(lineNumber, HighlighterLayer.FIRST, null);
                rangeHighlighter.setGutterIconRenderer(renderer);
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
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(TextConstants.DELETE_SAVED_NODES, TextConstants.DELETE_SESSION, project);
        if (isSure) {
            persistencyService.getContainers().clear();
        }
        return isSure;
    }

    public Optional<DebugNodeContainer> getNodeContainerByName(String nodeName) {
        return Optional.ofNullable(persistencyService.getContainers().get(nodeName));
    }

    public List<String> getSortedNodeNames(Integer lineNumber) {
        return persistencyService //
            .getContainers() //
            .entrySet() //
            .stream() //
            .sorted(getDebugContainerDateComparator())//
            .filter(entry -> entry.getValue().getLineNumber() == lineNumber) //
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
    }

    public List<String> getSortedNodeNames() {
        return persistencyService //
            .getContainers() //
            .entrySet() //
            .stream() //
            .sorted(getDebugContainerDateComparator())//
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
    }

    private Comparator<Map.Entry<String, DebugNodeContainer>> getDebugContainerDateComparator() {
        return Comparator.comparing(entry -> {
            LocalDateTime timestamp = entry.getValue().getTimestamp();
            return timestamp == null ? LocalDateTime.now() : timestamp;
        }, Comparator.reverseOrder());
    }

    public Comparator<DebugNodeContainer> getDebugContainerNodeDateComparator() {
        return Comparator.comparing(item -> {
            LocalDateTime timestamp = item.getTimestamp();
            return timestamp == null ? LocalDateTime.now() : timestamp;
        }, Comparator.reverseOrder());
    }

    public Map<String, DebugNodeContainer> getAllContainersPerNames() {
        return persistencyService.getContainers();
    }

    public void renameNode(String from, String to) {
        Map<String, DebugNodeContainer> containers = persistencyService.getContainers();
        DebugNodeContainer fromContainer = containers.get(from);
        containers.remove(from);
        fromContainer.setName(to);
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
            MessageDialogues.getYesNoMessageDialogue(String.format("Are you sure you want to export sessions: %n%s", getNodesMessage(selectedKeys)), TextConstants.EXPORT_SESSION, project);
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
                String nodeAsJson = nodeConverter.toString(namePerNode);
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
        FileChooserDescriptor jsonOnlyDescriptor = FileChooserDescriptorFactory.createMultipleFilesNoJarsDescriptor();
        jsonOnlyDescriptor.setRoots(ProjectUtil.guessProjectDir(project));
        VirtualFile @NotNull [] chosenFiles = FileChooser.chooseFiles(jsonOnlyDescriptor, project, null);

        if (Boolean.FALSE.equals(CheckLicense.isLicensed()) && chosenFiles.length + getAllContainersPerNames().size() >= 8) {
            MessageDialogues.getErrorMessageDialogue(GET_PAID_VERSION_IMPORT, project);
            CheckLicense.requestLicense(REGISTER_PLUGIN);
            return;
        }

        boolean containsNonJson =
            Arrays.stream(chosenFiles).anyMatch(chosenFile -> !chosenFile.getName().endsWith(".json"));
        if (containsNonJson) {
            MessageDialogues.getErrorMessageDialogue("One of the imported files is not a json file", project);
            return;
        }

        for (VirtualFile chosenFile : chosenFiles) {
            if (Objects.nonNull(chosenFile)) {
                String errorMessage = chosenFile.getName() + " file is empty";
                Map<String, DebugNodeContainer> nodes = persistencyService.getContainers();
                String fileName = Objects.requireNonNull(chosenFile.getNameWithoutExtension(), "File is not valid");
                if (nodes.containsKey(fileName)) {
                    MessageDialogues.getErrorMessageDialogue(
                        String.format("A session with name \"%s\" already exists", fileName), project);
                } else {
                    CharSequence charsSequence = Objects
                        .requireNonNull(FileDocumentManager.getInstance().getDocument(chosenFile), errorMessage)
                        .getCharsSequence();
                    String content = String.valueOf(charsSequence);
                    HashMap<String, DebugNodeContainer> nodeFromJson = nodeConverter.fromString(content);
                    DebugNodeContainer nodeContainer =
                        Objects.requireNonNull(nodeFromJson, errorMessage).entrySet().iterator().next().getValue();
                    save(fileName, nodeContainer);
                }
            }
        }
    }

    public DebugNodeContainer getCurrentSession(Project project) {
        SnapHandler snapHandler = new SnapHandler();
        return snapHandler //
            .getCurrentSession(project) //
            .orElseThrow(() -> new IllegalStateException("Why you messing?"));
    }

    public void renameFlowId(String from, String to) {
        persistencyService
            .getContainers()
            .entrySet()
            .stream()
            .filter(e -> from.equals(e.getValue().getFlowId()))
            .forEach(e -> e.getValue().setFlowId(to));
    }

    public void setFlowDate(LocalDateTime flowDate) {
        this.flowDate = flowDate;
    }
}
