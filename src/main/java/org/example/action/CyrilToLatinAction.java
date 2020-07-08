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

public class CyrilToLatinAction extends AnAction {
    private final MatnUzService matnUzService = new MatnUzService();

    public CyrilToLatinAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor)) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                String result = matnUzService.cyrilToLatin(selectedText);
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
        Project project = event.getData(CommonDataKeys.PROJECT);
        if(Objects.isNull(project))return;
        CommandProcessor.getInstance().executeCommand(project, () -> {
            CaretModel caretModel = editor.getCaretModel();
            if (caretModel != null) {
                caretModel.moveToOffset(editor.getSelectionModel().getLeadSelectionOffset());
                EditorModificationUtil.deleteSelectedText(editor);
                EditorModificationUtil.insertStringAtCaret(editor, changeText, true, false);
            }
        }, IdeBundle.message("command.go.to.next.split"), null);
    }

    private void showMessage(AnActionEvent event, String message) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Project currentProject = event.getProject();
            String dlgTitle = event.getPresentation().getDescription();
            Messages.showMessageDialog(currentProject, message, dlgTitle, Messages.getInformationIcon());
        });
    }
}
