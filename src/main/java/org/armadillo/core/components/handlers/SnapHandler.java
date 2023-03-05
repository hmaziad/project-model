package org.armadillo.core.components.handlers;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.armadillo.core.constants.MessageDialogues;
import org.armadillo.core.tree.components.DebugFrame;
import org.armadillo.core.tree.components.DebugNode;
import org.armadillo.core.tree.components.DebugNodeContainer;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.frame.XDebuggerFramesList;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XDebuggerTreeNode;
import com.sun.jdi.Location;

import lombok.extern.log4j.Log4j2;

import static org.armadillo.core.util.HelperUtil.getPackageNameFromVfs;

@Log4j2
public class SnapHandler {

    public Optional<DebugNodeContainer> getCurrentSession(Project project) {
        XDebuggerManager xDebuggerManager = XDebuggerManager.getInstance(project);
        XDebugSession currentSession = xDebuggerManager.getCurrentSession();
        if (!Objects.isNull(currentSession) && Objects.nonNull(currentSession.getCurrentStackFrame())) {
            XSourcePosition sourcePosition = currentSession.getCurrentStackFrame().getSourcePosition();
            int lineNumber = sourcePosition.getLine();
            LOG.info("Debugger session exists");
            XDebuggerTreeNode xRootNode;
            try {
                xRootNode = getDebugSessionTree(currentSession);
            } catch (Exception e) {
                MessageDialogues.getErrorMessageDialogue("Could not save debugger variables. Please contact 'armadillo.developers@gmail.com' with version of intellij you have. Please right click on the node and 'Save in Armadillo' as a workaround", project);
                return Optional.empty();
            }
            LOG.debug("Debugger session retrieved: {}", xRootNode);
            DebugNode resultNode = new DebugNode(xRootNode);
            List<DebugFrame> frames = getFrames(currentSession);

            Pair<Integer, String> methodPair = getTextBlock(project, sourcePosition, lineNumber);

            DebugNodeContainer container = DebugNodeContainer.builder() //
                .frames(frames) //
                .lineIndexDebugged(methodPair.getFirst())
                .textBlock(methodPair.getSecond()) //
                .node(resultNode) //
                .lineNumber(lineNumber) //
                .packageName(getPackageNameFromVfs(sourcePosition.getFile(), project).orElse(null)) //
                .build();

            return Optional.of(container);
        }

        return Optional.empty();
    }

    private Pair<Integer, String> getTextBlock(Project project, XSourcePosition sourcePosition, int lineNumber) {
        VirtualFile file = sourcePosition.getFile();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        PsiElement element = psiFile.findElementAt(document.getLineStartOffset(lineNumber));
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        int offset = containingMethod.getTextRange().getStartOffset();
        int lineNumberContainerMethod = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumberContainerMethod);
        int lineEndOffset = document.getLineEndOffset(lineNumberContainerMethod);
        String methodText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        int lineInBlock = lineNumber - lineNumberContainerMethod;
        String methodBlock = methodText.replace('{', ' ') + element.getParent().getText();
        return new Pair<>(lineInBlock, methodBlock);
    }

    private List<DebugFrame> getFrames(XDebugSession currentSession) {
        Content framesContent = Arrays //
            .stream(currentSession.getUI().getContents()) //
            .filter(c -> "Frames".equals(c.getDisplayName())) //
            .findFirst() //
            .orElseThrow();
        JComponent component = (JComponent) Arrays //
            .stream(framesContent.getComponent().getComponents()) //
            .filter(JBScrollPane.class::isInstance) //
            .findFirst() //
            .orElseThrow(); //
        JComponent component1 = (JComponent) Arrays //
            .stream(component.getComponents()) //
            .filter(JBViewport.class::isInstance) //
            .findFirst() //
            .orElseThrow(); //
        XDebuggerFramesList component2 = (XDebuggerFramesList) component1.getComponents()[0]; // only one

        List<?> items = component2.getModel().getItems();
        List<DebugFrame> debugFrames = new ArrayList<>();
        for (Object item : items) {
            JavaStackFrame stackFrame = (JavaStackFrame) item;
            Location location = stackFrame.getDescriptor().getLocation();
            if (Objects.nonNull(location)) {
                String methodName = location.method().name();
                int lineNumber = location.lineNumber();
                String packageWithName = location.declaringType().name();
                debugFrames.add(new DebugFrame(methodName, lineNumber, packageWithName));
            }
        }
        return debugFrames;
    }

    private XDebuggerTreeNode getDebugSessionTree(XDebugSession currentSession) {
        RunnerLayoutUi uiComponent = currentSession.getUI();
        JComponent variablesFrame = uiComponent.getContents()[1].getComponent();
        JComponent childJPanel = getChildJPanel(variablesFrame).orElseThrow(); // ?
        Component[] components = childJPanel.getComponents(); // ?
        JComponent extraComponent = null;
        if (components.length == 1 && components[0] instanceof BorderLayoutPanel) {
            extraComponent = (JComponent) components[0];
        }

        JComponent scrollPane = getScrollPane(Objects.nonNull(extraComponent) ? extraComponent.getComponents() : components).orElseThrow();
        components = scrollPane.getComponents();
        JBViewport viewport = null;
        for (var component : components) {
            if (component instanceof JBViewport) {
                viewport = (JBViewport) component;
            }
        }
        return (XDebuggerTreeNode) ((XDebuggerTree) viewport.getComponent(0)).getTreeModel().getRoot();
    }

    private Optional<JComponent>  getScrollPane(Component[] components) {
        for (Component component : components) {
            if (component instanceof JBScrollPane) {
                return Optional.of((JBScrollPane) component);
            }
        }
        return Optional.empty();
    }

    private Optional<JComponent> getChildJPanel(JComponent variablesFrame) {
        Component[] components = variablesFrame.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                return Optional.of((JPanel)component);
            }
        }
        return Optional.empty();
    }

}