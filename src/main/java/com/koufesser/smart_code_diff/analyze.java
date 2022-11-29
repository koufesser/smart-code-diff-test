package com.koufesser.smart_code_diff;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class analyze extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }
        int offset = editor.getCaretModel().getOffset();

        final StringBuilder infoBuilder = new StringBuilder();

        PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            infoBuilder
                    .append("Containing method: ")
                    .append(containingMethod != null ? containingMethod.getName() : "none")
                    .append("\n");
            if (containingMethod != null) {
                PsiClass containingClass = containingMethod.getContainingClass();
                infoBuilder
                        .append("Containing class: ")
                        .append(containingClass != null ? containingClass.getName() : "none")
                        .append("\n");

            }
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
        infoBuilder.append("This file contains ");
        int count = 0;
        PsiClass[] javaFileClasses = psiJavaFile.getClasses();
        for (PsiClass javaFileClass : javaFileClasses) {
            count += javaFileClass.getMethods().length;
        }
        infoBuilder.append(count);
        infoBuilder.append(" methods");

        Messages.showMessageDialog(anActionEvent.getProject(), infoBuilder.toString(), "PSI Info", null);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabled(editor != null && psiFile != null);
    }

}