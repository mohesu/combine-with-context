package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ui.TextTransferable
import com.mohesu.combinecontext.utils.ContextFormatter

class CopyToClipboardAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        var selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        
        // If no files selected from project view, try to get current file from editor
        if (selectedFiles.isNullOrEmpty()) {
            val currentFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
            if (currentFile != null) {
                selectedFiles = arrayOf(currentFile)
            }
        }
        
        // As a last resort, try to get files from open editors
        if (selectedFiles.isNullOrEmpty()) {
            val fileEditorManager = FileEditorManager.getInstance(project)
            val openFiles = fileEditorManager.openFiles
            if (openFiles.isNotEmpty()) {
                selectedFiles = openFiles
            }
        }

        if (selectedFiles.isNullOrEmpty()) {
            Messages.showWarningDialog(project, "No files available to process. Please select files in the project view or open a file in the editor.", "Combine with Context")
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
        if (project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        
        // Check multiple sources for available files
        val selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val currentFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val hasOpenFiles = FileEditorManager.getInstance(project).openFiles.isNotEmpty()
        
        // Enable if we have selected files, current file, or open files
        val hasAvailableFiles = !selectedFiles.isNullOrEmpty() || currentFile != null || hasOpenFiles
        e.presentation.isEnabledAndVisible = hasAvailableFiles
    }
}