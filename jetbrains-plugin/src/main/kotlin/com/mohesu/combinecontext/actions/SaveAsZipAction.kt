package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.mohesu.combinecontext.settings.CombineContextSettings
import com.mohesu.combinecontext.utils.ContextFormatter
import java.io.File

class SaveAsZipAction : AnAction() {
    
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
            
            val settings = CombineContextSettings.getInstance()
            val outputPath = formatter.getOutputPath(settings.zipFileName)
            
            // Create backup if file exists
            createBackup(outputPath, project)
            
            formatter.createZipArchive(files, outputPath)
            
            Messages.showInfoMessage(
                project,
                "Saved ${files.size} files to ZIP archive: $outputPath",
                "Combine with Context"
            )
            
            // Store for update functionality
            LastActionStorage.setLastAction("zip", selectedFiles, files.size)
            
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to create ZIP archive: ${e.message}",
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