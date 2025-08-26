package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.mohesu.combinecontext.settings.CombineContextSettings
import com.mohesu.combinecontext.utils.ContextFormatter
import java.io.File

class UndoLastSaveAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        val lastAction = LastActionStorage.getLastAction()
        if (lastAction == null) {
            Messages.showWarningDialog(
                project,
                "No previous save action to undo.",
                "Combine with Context"
            )
            return
        }
        
        val (actionType, _, _) = lastAction
        if (actionType == "clipboard") {
            Messages.showWarningDialog(
                project,
                "Cannot undo clipboard action. Only file save actions can be undone.",
                "Combine with Context"
            )
            return
        }
        
        try {
            val settings = CombineContextSettings.getInstance()
            val formatter = ContextFormatter(project)
            val historyDir = formatter.getOutputPath(settings.historyFolder)
            
            val currentFileName = when (actionType) {
                "paste" -> settings.outputFileName
                "zip" -> settings.zipFileName
                else -> {
                    Messages.showWarningDialog(project, "Unknown action type to undo", "Combine with Context")
                    return
                }
            }
            
            val currentFilePath = formatter.getOutputPath(currentFileName)
            val currentFile = File(currentFilePath)
            
            // Find the most recent backup
            val historyDirFile = File(historyDir)
            if (!historyDirFile.exists()) {
                Messages.showWarningDialog(
                    project,
                    "No backup history found.",
                    "Combine with Context"
                )
                return
            }
            
            val baseName = File(currentFileName).nameWithoutExtension
            val extension = File(currentFileName).extension
            val backupPattern = "${baseName}_\\d{4}_\\d{2}_\\d{2}T\\d{2}_\\d{2}_\\d{2}\\.$extension"
            
            val backupFiles = historyDirFile.listFiles { _, name ->
                name.matches(Regex(backupPattern))
            }?.sortedByDescending { it.lastModified() }
            
            if (backupFiles.isNullOrEmpty()) {
                Messages.showWarningDialog(
                    project,
                    "No backup files found for $currentFileName",
                    "Combine with Context"
                )
                return
            }
            
            val mostRecentBackup = backupFiles.first()
            
            // Create a backup of the current file before restoring
            if (currentFile.exists()) {
                val timestamp = com.mohesu.combinecontext.utils.FileUtils.timestampString()
                val tempBackupName = "${baseName}_before_undo_${timestamp}.$extension"
                val tempBackupFile = File(historyDir, tempBackupName)
                currentFile.copyTo(tempBackupFile, overwrite = true)
            }
            
            // Restore the backup
            mostRecentBackup.copyTo(currentFile, overwrite = true)
            
            // Delete the used backup
            mostRecentBackup.delete()
            
            // Refresh the file system
            LocalFileSystem.getInstance().refreshAndFindFileByPath(currentFilePath)
            
            Messages.showInfoMessage(
                project,
                "Successfully restored $currentFileName from backup",
                "Combine with Context"
            )
            
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to undo last save: ${e.message}",
                "Combine with Context Error"
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        val lastAction = LastActionStorage.getLastAction()
        val canUndo = project != null && lastAction != null && lastAction.first != "clipboard"
        e.presentation.isEnabledAndVisible = canUndo
    }
}