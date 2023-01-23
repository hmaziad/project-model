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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.intellij.sdk.project.model.constants.MessageDialogues;
import org.intellij.sdk.project.model.tree.components.DebugNode;
import org.intellij.sdk.project.model.util.DebugNodeConverter;
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
    private final DebugNodeConverter nodeConverter = new DebugNodeConverter();

    public void save(DebugNode debugNode, Project project) {
        LocalDateTime timestamp = LocalDateTime.now();
        debugNode.setTimestamp(timestamp);
        String generatedName = getGenerateName(project, timestamp);
        save(generatedName, debugNode);
    }

    private String getGenerateName(Project project, LocalDateTime timestamp) {
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern(NODE_DATE_FORMAT));
        XSourcePosition xSourcePosition = XDebuggerManager.getInstance(project).getCurrentSession().getCurrentPosition();
        String classPart = xSourcePosition.getFile().getNameWithoutExtension();
        return String.format(GENERATED_SESSION_NAME, classPart, formattedTimestamp);
    }

    private void save(String nodeName, DebugNode debugNode) {
        COMPONENT_SERVICE.setNodeNameInWindow(nodeName);
        PERSISTENCY_SERVICE.getNodes().put(nodeName, debugNode);
    }

    public void delete(String nodeName, Project project) {
        String message = String.format(DELETE_SAVED_NODE, nodeName);
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(message, DELETE_SESSION, project);
        if (isSure) {
            PERSISTENCY_SERVICE.getNodes().remove(nodeName);
            COMPONENT_SERVICE.getDebugTreeManager().setRoot(null);
            COMPONENT_SERVICE.setNodeNameInWindow(null);
        }
    }

    public void deleteAll(Project project) {
        boolean isSure = MessageDialogues.getYesNoMessageDialogue(DELETE_SAVED_NODES, DELETE_SESSION, project);
        if (isSure) {
            PERSISTENCY_SERVICE.getNodes().clear();
        }
    }

    public DebugNode getNodeByName(String nodeName) {
        return PERSISTENCY_SERVICE.getNodes().get(nodeName);
    }

    public List<DebugNode> getAllNodes() {
        return new ArrayList<>(PERSISTENCY_SERVICE //
            .getNodes() //
            .values());
    }

    public List<String> getAllNodeNames() {
        Comparator<Map.Entry<String, DebugNode>> debugComparator = Comparator.comparing(e1 -> e1.getValue().getTimestamp());
        return PERSISTENCY_SERVICE //
            .getNodes() //
            .entrySet() //
            .stream() //
            .filter(e -> Objects.nonNull(e.getValue().getTimestamp()))
            .sorted(Comparator.nullsLast(debugComparator))//
            .filter(Objects::nonNull) //
            .map(Map.Entry::getKey) //
            .collect(Collectors.toList());
    }

    public Map<String, DebugNode> getAllNodesPerNames() {
        return PERSISTENCY_SERVICE.getNodes();
    }

    public void renameNode(String from, String to) {
        Map<String, DebugNode> nodes = PERSISTENCY_SERVICE.getNodes();
        DebugNode fromNode = nodes.get(from);
        nodes.remove(from);
        nodes.put(to, fromNode);
    }

    /**
     * Feedback node saved, and say path
     * tell of node already exists
     * Show error in case of error
     */


    public void export(String selectedKey, Project project) {
        String path = project.getBasePath();
        DebugNode debugNode = getNodeByName(selectedKey);
        HashMap<String, DebugNode> namePerNode = new HashMap<>();
        namePerNode.put("root", debugNode);
        String nodeAsJson = this.nodeConverter.toString(namePerNode);
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
            String fileName = Objects.requireNonNull(chosenFile.getNameWithoutExtension(), "File is not valid");
            Map<String, DebugNode> nodes = PERSISTENCY_SERVICE.getNodes();
            if (nodes.containsKey(fileName)) {
                MessageDialogues.getErrorMessageDialogue(String.format("A session with name \"%s\" already exists", fileName), project);
            } else {
                CharSequence charsSequence = Objects.requireNonNull(FileDocumentManager.getInstance().getDocument(chosenFile), errorMessage).getCharsSequence();
                String content = String.valueOf(charsSequence);
                HashMap<String, DebugNode> nodeFromJson = this.nodeConverter.fromString(content);
                DebugNode debugNode = Objects.requireNonNull(nodeFromJson, errorMessage).entrySet().iterator().next().getValue();
                save(fileName, debugNode);
            }
        }
    }
}
