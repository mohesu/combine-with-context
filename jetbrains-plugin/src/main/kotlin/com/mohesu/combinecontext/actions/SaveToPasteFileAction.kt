package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.mohesu.combinecontext.settings.CombineContextSettings
import com.mohesu.combinecontext.utils.ContextFormatter
import java.io.File

class SaveToPasteFileAction : AnAction() {
    
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
            
            val markdown = formatter.formatAsMarkdown(files)
            val settings = CombineContextSettings.getInstance()
            val outputPath = formatter.getOutputPath(settings.outputFileName)
            
            // Create backup if file exists
            createBackup(outputPath, project)
            
            formatter.saveToFile(markdown, outputPath)
            
            // Open the file if requested
            if (settings.openAfterSave) {
                val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputPath)
                virtualFile?.let {
                    FileEditorManager.getInstance(project).openFile(it, true)
                }
            }
            
            Messages.showInfoMessage(
                project,
                "Saved ${files.size} files to $outputPath",
                "Combine with Context"
            )
            
            // Store for update functionality
            LastActionStorage.setLastAction("paste", selectedFiles, files.size)
            
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to save paste file: ${e.message}",
                "Combine with Context Error"
            )
        }
    }
    
    private fun createBackup(outputPath: String, project: com.intellij.openapi.project.Project) {
        val outputFile = File(outputPath)
        if (!outputFile.exists()) return
        
        val settings = CombineContextSettings.getInstance()
        val formatter = ContextFormatter(project)
        val historyDir = formatter.getOutputPath(settings.historyFolder)
        
        try {
            File(historyDir).mkdirs()
            val timestamp = com.mohesu.combinecontext.utils.FileUtils.timestampString()
            val backupName = "${outputFile.nameWithoutExtension}_$timestamp.${outputFile.extension}"
            val backupFile = File(historyDir, backupName)
            outputFile.copyTo(backupFile, overwrite = true)
        } catch (e: Exception) {
            // Backup failed, but continue with save
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        val selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        e.presentation.isEnabledAndVisible = project != null && !selectedFiles.isNullOrEmpty()
    }
}