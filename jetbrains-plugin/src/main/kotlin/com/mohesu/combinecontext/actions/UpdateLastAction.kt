// kotlin
package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.ui.TextTransferable
import com.mohesu.combinecontext.settings.CombineContextSettings
import com.mohesu.combinecontext.utils.ContextFormatter

class UpdateLastAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val lastAction = LastActionStorage.getLastAction()
        if (lastAction == null) {
            Messages.showWarningDialog(
                project,
                "No previous action to update. Please perform a save or copy action first.",
                "Combine with Context"
            )
            return
        }

        val (rawActionType, selectedFiles, _) = lastAction
        if (selectedFiles == null) return
        val actionType = requireNotNull(rawActionType) { "Stored actionType was null" }

        try {
            val formatter = ContextFormatter(project)
            val files = formatter.collectFiles(selectedFiles)

            if (files.isEmpty()) {
                Messages.showWarningDialog(project, "No valid files found to process", "Combine with Context")
                return
            }

            when (actionType) {
                "clipboard" -> {
                    val markdown: CharSequence = formatter.formatAsMarkdown(files)
                    val copyPasteManager = CopyPasteManager.getInstance()
                    copyPasteManager.setContents(TextTransferable(markdown))

                    Messages.showInfoMessage(
                        project,
                        "Updated clipboard with ${files.size} files",
                        "Combine with Context"
                    )
                }
                "paste" -> {
                    val markdown = formatter.formatAsMarkdown(files)
                    val settings = CombineContextSettings.getInstance()
                    val outputPath = formatter.getOutputPath(settings.outputFileName)

                    formatter.saveToFile(markdown, outputPath)

                    if (settings.openAfterSave) {
                        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputPath)
                        virtualFile?.let {
                            FileEditorManager.getInstance(project).openFile(it, true)
                        }
                    }

                    Messages.showInfoMessage(
                        project,
                        "Updated paste file with ${files.size} files",
                        "Combine with Context"
                    )
                }
                "zip" -> {
                    val settings = CombineContextSettings.getInstance()
                    val outputPath = formatter.getOutputPath(settings.zipFileName)

                    formatter.createZipArchive(files, outputPath)

                    Messages.showInfoMessage(
                        project,
                        "Updated ZIP archive with ${files.size} files",
                        "Combine with Context"
                    )
                }
            }

            LastActionStorage.setLastAction(actionType, selectedFiles, files.size)

        } catch (ex: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to update last action: ${ex.message}",
                "Combine with Context Error"
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val hasLastAction = LastActionStorage.hasLastAction()
        e.presentation.isEnabledAndVisible = project != null && hasLastAction
        e.presentation.text = if (hasLastAction) {
            val (storedType, _, _) = LastActionStorage.getLastAction() ?: return
            when (storedType) {
                "clipboard" -> "CC: Update Clipboard"
                "paste" -> "CC: Update Paste File"
                "zip" -> "CC: Update ZIP Archive"
                else -> "CC: Update Last Output"
            }
        } else {
            "CC: Update Last Output"
        }
    }
}