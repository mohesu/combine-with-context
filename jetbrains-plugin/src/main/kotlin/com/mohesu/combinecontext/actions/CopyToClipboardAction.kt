package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.TextTransferable
import com.mohesu.combinecontext.utils.ContextFormatter

class CopyToClipboardAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return

        if (selectedFiles.isEmpty()) {
            Messages.showWarningDialog(project, "No files selected", "Combine with Context")
            return
        }

        try {
            val formatter = ContextFormatter(project)
            val files = formatter.collectFiles(selectedFiles)

            if (files.isEmpty()) {
                Messages.showWarningDialog(project, "No valid files found to process", "Combine with Context")
                return
            }

            // Force CharSequence type to resolve overload
            val markdown: CharSequence = formatter.formatAsMarkdown(files)
            val copyPasteManager = CopyPasteManager.getInstance()
            copyPasteManager.setContents(TextTransferable(markdown))

            Messages.showInfoMessage(
                project,
                "Copied ${files.size} files to clipboard",
                "Combine with Context"
            )

            LastActionStorage.setLastAction("clipboard", selectedFiles, files.size)

        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to copy to clipboard: ${e.message}",
                "Combine with Context Error"
            )
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        e.presentation.isEnabledAndVisible = project != null && !selectedFiles.isNullOrEmpty()
    }
}