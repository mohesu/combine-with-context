package com.mohesu.combinecontext.utils

import com.mohesu.combinecontext.settings.CombineContextSettings
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class FileInfo(
    val virtualFile: VirtualFile,
    val relativePath: String,
    val content: String
)

class ContextFormatter(private val project: Project) {
    private val settings = CombineContextSettings.getInstance()
    
    fun collectFiles(selectedFiles: Array<VirtualFile>): List<FileInfo> {
        val projectRoot = project.basePath ?: return emptyList()
        val gitIgnoreFilter = if (settings.useGitignore) {
            GitIgnoreFilter.fromFile(projectRoot)
        } else null
        
        val files = mutableListOf<FileInfo>()
        val processed = mutableSetOf<String>()
        
        for (selectedFile in selectedFiles) {
            collectFilesRecursively(selectedFile, projectRoot, gitIgnoreFilter, files, processed)
        }
        
        return files.sortedBy { it.relativePath }
    }
    
    private fun collectFilesRecursively(
        file: VirtualFile,
        projectRoot: String,
        gitIgnoreFilter: GitIgnoreFilter?,
        files: MutableList<FileInfo>,
        processed: MutableSet<String>
    ) {
        val filePath = file.path
        if (processed.contains(filePath)) return
        processed.add(filePath)
        
        val relativePath = try {
            Paths.get(projectRoot).relativize(Paths.get(filePath)).toString().replace('\\', '/')
        } catch (e: Exception) {
            return
        }
        
        // Check gitignore
        if (gitIgnoreFilter?.isIgnored(relativePath) == true) return
        
        if (file.isDirectory) {
            file.children?.forEach { child ->
                collectFilesRecursively(child, projectRoot, gitIgnoreFilter, files, processed)
            }
        } else {
            // File filtering
            if (isFileFiltered(file, relativePath)) return
            
            try {
                val content = String(file.contentsToByteArray(), Charsets.UTF_8)
                if (content.trim().isEmpty()) return
                
                val processedContent = if (settings.compressContent) {
                    val lang = FileUtils.getMarkdownLangForFile(file.name, settings.markdownMapping)
                    FileUtils.minifyContent(content, lang)
                } else {
                    FileUtils.stripControlChars(content).replace("\r\n", "\n")
                }
                
                files.add(FileInfo(file, relativePath, processedContent))
            } catch (e: Exception) {
                // Skip files that can't be read
            }
        }
    }
    
    private fun isFileFiltered(file: VirtualFile, relativePath: String): Boolean {
        // Check file extension filter
        val extension = "." + (file.extension?.lowercase() ?: "")
        if (settings.filteredExtensions.contains(extension)) return true
        
        // Check file size
        if (file.length > settings.maxFileSize) return true
        
        // Check if binary file
        try {
            val content = file.contentsToByteArray()
            if (FileUtils.isBinaryFile(content)) return true
        } catch (e: Exception) {
            return true
        }
        
        return false
    }
    
    fun formatAsMarkdown(files: List<FileInfo>): String {
        val output = StringBuilder()
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        
        if (settings.includeFileTree) {
            val tree = FileUtils.buildFileTree(files.map { it.relativePath })
            output.append("## File Tree @ ${now.format(formatter)}\n```\n$tree\n```\n\n")
        }
        
        if (settings.includeFileAnalysis) {
            output.append("## File Analysis @ ${now.format(formatter)}\n")
            output.append("- Total files: ${files.size}\n")
            
            val extCounts = files.groupingBy { 
                val ext = File(it.relativePath).extension.lowercase()
                if (ext.isEmpty()) "no extension" else ".$ext"
            }.eachCount()
            
            extCounts.entries.sortedBy { it.key }.forEach { (ext, count) ->
                output.append("- $ext: $count\n")
            }
            output.append("\n${settings.separator}")
        }
        
        for (fileInfo in files) {
            val lang = FileUtils.getMarkdownLangForFile(fileInfo.relativePath, settings.markdownMapping)
            val fence = FileUtils.getBestFence(fileInfo.content)
            
            output.append("#### ${fileInfo.relativePath}")
            if (settings.includeTimestamp) {
                output.append(" @ ${now.format(formatter)}")
            }
            output.append("\n$fence$lang\n${fileInfo.content}\n$fence\n${settings.separator}")
        }
        
        return output.toString()
    }
    
    fun createZipArchive(files: List<FileInfo>, outputPath: String) {
        try {
            FileUtils.ensureDirExists(File(outputPath).parent)
            
            ZipOutputStream(FileOutputStream(outputPath)).use { zip ->
                for (fileInfo in files) {
                    val entry = ZipEntry(fileInfo.relativePath)
                    zip.putNextEntry(entry)
                    zip.write(fileInfo.content.toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to create ZIP archive: ${e.message}", e)
        }
    }
    
    fun saveToFile(content: String, outputPath: String) {
        try {
            FileUtils.ensureDirExists(File(outputPath).parent)
            
            if (settings.appendMode && File(outputPath).exists()) {
                File(outputPath).appendText("\n\n$content")
            } else {
                File(outputPath).writeText(content)
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to save file: ${e.message}", e)
        }
    }
    
    fun getOutputPath(fileName: String): String {
        val projectRoot = project.basePath ?: ""
        val subfolder = if (settings.outputSubfolder.isNotEmpty()) {
            File(projectRoot, settings.outputSubfolder).path
        } else {
            projectRoot
        }
        return File(subfolder, fileName).path
    }
}