package org.intellij.sdk.project.model.components.handlers;

import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODE;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SAVED_NODES;
import static org.intellij.sdk.project.model.constants.TextConstants.DELETE_SESSION;
import static org.intellij.sdk.project.model.constants.TextConstants.GENERATED_SESSION_NAME;
import static org.intellij.sdk.project.model.constants.TextConstants.NODE_DATE_FORMAT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.tree.components.DebugNodeContainer;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;

public class NodeHandler implements ReachServices {

    public void save(DebugNode debugNode, Project project) {
        LocalDateTime timestamp = LocalDateTime.now();
        String generatedName = getGenerateName(project, timestamp);
        DebugNodeContainer nodeContainer = new DebugNodeContainer(timestamp, null, debugNode);
        save(generatedName, nodeContainer);
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

    public void delete(String nodeName, Project project) {
        String message = String.format(DELETE_SAVED_NODE, nodeName);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, DELETE_SESSION, project);
        if (isSure) {
            PERSISTENCY_SERVICE.getContainers().remove(nodeName);
            COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
            COMPONENT_SERVICE.setNodeNameInWindow(null);
        }
    }

    public void deleteAll(Project project) {
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(DELETE_SAVED_NODES, DELETE_SESSION, project);
        if (isSure) {
            PERSISTENCY_SERVICE.getContainers().clear();
        }
    }

    public Optional<DebugNodeContainer> getNodeContainerByName(String nodeName) {
        return Optional.ofNullable(PERSISTENCY_SERVICE.getContainers().get(nodeName));
    }

    public List<String> getAllNodeNames() {
        Comparator<Map.Entry<String, DebugNodeContainer>> debugComparator = Comparator.comparing(e1 -> {
            LocalDateTime timestamp = e1.getValue().getTimestamp();
            return timestamp == null ? LocalDateTime.now() : timestamp;
        }, Comparator.reverseOrder());
        return PERSISTENCY_SERVICE //
            .getContainers() //
            .entrySet() //
            .stream() //
            .sorted(debugComparator)//
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
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

    public void export(String selectedKey, Project project) {
        String path = project.getBasePath();
        DebugNodeContainer nodeContainer = getNodeContainerByName(selectedKey).orElseThrow();
        HashMap<String, DebugNodeContainer> namePerNode = new HashMap<>();
        namePerNode.put("root", nodeContainer);
        String nodeAsJson = COMPONENT_SERVICE.getNodeConverter().toString(namePerNode);
        String fileName = selectedKey.replace(":", "-") + ".json";
        Path pathWithDir = createDirectoryIfNotExists(path);
        try {
            String fullPath = String.format("%s%s", pathWithDir.toUri().getPath(), fileName);
            File nodeAsFile = new File(fullPath);
            if (nodeAsFile.createNewFile()) {
                MessageDialogues.showInfoMessageDialogue("File created under path:\n" + fullPath, "Success");
            } else {
                String errorMessage =
                    String.format("File with name \"%s\" already exists under directory:%n%s%nPlease rename session or move the existing file.", fileName, pathWithDir);
                MessageDialogues.getErrorMessageDialogue(errorMessage, project);
            }
            FileWriter myWriter = new FileWriter(nodeAsFile);
            myWriter.write(Objects.requireNonNull(nodeAsJson, "Session contains no data"));
            myWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException("Could not export session", e);
        }
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
            }else {
            CharSequence charsSequence = Objects.requireNonNull(FileDocumentManager.getInstance().getDocument(chosenFile), errorMessage).getCharsSequence();
            String content = String.valueOf(charsSequence);
            HashMap<String, DebugNodeContainer> nodeFromJson = COMPONENT_SERVICE.getNodeConverter().fromString(content);
            DebugNodeContainer nodeContainer = Objects.requireNonNull(nodeFromJson, errorMessage).entrySet().iterator().next().getValue();
            save(fileName, nodeContainer);
            }
        }
    }

    public DebugNodeContainer getCurrentSession(Project project) {
        DebugNode debugNode = COMPONENT_SERVICE //
            .getSnapHandler() //
            .getCurrentSession(project) //
            .orElseThrow(() -> new IllegalStateException("Why you messing?"));

        return new DebugNodeContainer(null, null, debugNode);
    }

}
