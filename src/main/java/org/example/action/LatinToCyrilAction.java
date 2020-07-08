package org.example.action;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.example.service.MatnUzService;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LatinToCyrilAction extends AnAction {
    private final MatnUzService matnUzService = new MatnUzService();

    public LatinToCyrilAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                String result = matnUzService.latinToCyril(selectedText);
                replaceMassage(event, editor, result);
            } catch (Exception e) {
                showMessage(event, e.getMessage());
                e.printStackTrace();
            }
        });
    }


    @Override
    public boolean isDumbAware() {
        System.out.println("isDumbAware");
        return false;
    }

    private void replaceMassage(AnActionEvent event, Editor editor, String changeText) {
        final Project project = CommonDataKeys.PROJECT.getData(event.getDataContext());
        CommandProcessor.getInstance().executeCommand(project, () -> {
            CaretModel caretModel = editor.getCaretModel();
            caretModel.moveToOffset(editor.getSelectionModel().getLeadSelectionOffset());
            EditorModificationUtil.deleteSelectedText(editor);
            EditorModificationUtil.insertStringAtCaret(editor, changeText, true, false);
        }, IdeBundle.message("command.go.to.next.split"), null);
    }

    private void showMessage(AnActionEvent event, String message) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Project currentProject = event.getProject();
            Messages.showMessageDialog(currentProject, message, "Message", Messages.getInformationIcon());
        });
    }
}
