package com.mohesu.combinecontext.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.mohesu.combinecontext.settings.CombineContextSettings
import com.mohesu.combinecontext.utils.ContextFormatter
import java.io.File

class SaveAsZipAction : AnAction() {
    
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
    
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        
        // Check multiple sources for available files (in order of preference)
        val selectedFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val currentFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        // Enable if we have selected files or current file
        val hasAvailableFiles = !selectedFiles.isNullOrEmpty() || currentFile != null
        e.presentation.isEnabledAndVisible = hasAvailableFiles
        
        // Update action text based on context for better user understanding
        if (hasAvailableFiles) {
            e.presentation.text = when {
                !selectedFiles.isNullOrEmpty() && selectedFiles.size > 1 -> "CC: Save ${selectedFiles.size} Files as ZIP"
                !selectedFiles.isNullOrEmpty() && selectedFiles.size == 1 -> "CC: Save Selected File as ZIP"
                currentFile != null -> "CC: Save Current File as ZIP"
                else -> "CC: Save as ZIP"
            }
        }
    }
}