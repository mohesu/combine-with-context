package com.mohesu.combinecontext.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "CombineContextSettings",
    storages = [Storage("CombineContextSettings.xml")]
)
@Service(Service.Level.APPLICATION)
class CombineContextSettings : PersistentStateComponent<CombineContextSettings> {

    var zipFileName: String = "context.zip"
    var outputFileName: String = "paste.md"
    var appendMode: Boolean = false
    var includeTimestamp: Boolean = true
    var filteredExtensions: MutableList<String> = mutableListOf(".png", ".jpg", ".jpeg", ".gif", ".exe", ".dll", ".ico", ".svg", ".webp", ".bmp", ".tiff", ".zip", ".tar")
    var maxFileSize: Long = 5242880L // 5MB
    var outputSubfolder: String = ""
    var openAfterSave: Boolean = true
    var separator: String = "\n---\n"
    var useGitignore: Boolean = true
    var historyFolder: String = ".llm-context-history"
    var includeFileTree: Boolean = true
    var includeFileAnalysis: Boolean = true
    var symlinkHandling: String = "skip" // 'skip' | 'resolve'
    var compressContent: Boolean = false
    
    // Markdown language mappings (extension -> language) - updated to match VS Code defaults
    var markdownMapping: MutableMap<String, String> = mutableMapOf(
        ".md" to "markdown",
        ".js" to "javascript",
        ".ts" to "typescript",
        ".json" to "json",
        ".py" to "python",
        ".css" to "css",
        ".sh" to "bash",
        ".yml" to "yaml",
        ".yaml" to "yaml",
        ".dart" to "dart",
        ".java" to "java",
        ".kt" to "kotlin",
        ".xml" to "xml",
        ".html" to "html",
        ".php" to "php",
        ".rb" to "ruby",
        ".go" to "go",
        ".rs" to "rust",
        ".cpp" to "cpp",
        ".c" to "c",
        ".h" to "c",
        ".hpp" to "cpp"
    )

    override fun getState(): CombineContextSettings = this

    override fun loadState(state: CombineContextSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): CombineContextSettings {
            return ApplicationManager.getApplication().getService(CombineContextSettings::class.java)
        }
    }
}