package com.mohesu.combinecontext.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class CombineContextConfigurable : Configurable {
    private var component: DialogPanel? = null
    private val settings = CombineContextSettings.getInstance()
    
    // Temporary variables to hold form values
    private var zipFileName = settings.zipFileName
    private var outputFileName = settings.outputFileName
    private var appendMode = settings.appendMode
    private var includeTimestamp = settings.includeTimestamp
    private var filteredExtensions = settings.filteredExtensions.joinToString(", ")
    private var maxFileSize = (settings.maxFileSize / 1024 / 1024).toInt() // Convert to MB
    private var outputSubfolder = settings.outputSubfolder
    private var openAfterSave = settings.openAfterSave
    private var separator = settings.separator
    private var useGitignore = settings.useGitignore
    private var historyFolder = settings.historyFolder
    private var includeFileTree = settings.includeFileTree
    private var includeFileAnalysis = settings.includeFileAnalysis
    private var symlinkHandling = settings.symlinkHandling
    private var compressContent = settings.compressContent

    override fun getDisplayName(): String = "Combine with Context"

    override fun createComponent(): JComponent {
        component = panel {
            group("Output Settings") {
                row("Output file name:") {
                    textField()
                        .bindText(::outputFileName)
                        .comment("Name or relative path of the output markdown file")
                }
                row("ZIP file name:") {
                    textField()
                        .bindText(::zipFileName)
                        .comment("Name or relative path for the ZIP archive")
                }
                row("Output subfolder:") {
                    textField()
                        .bindText(::outputSubfolder)
                        .comment("Subfolder in the project root for output files")
                }
                row("History folder:") {
                    textField()
                        .bindText(::historyFolder)
                        .comment("Folder for storing backup files")
                }
                row {
                    checkBox("Append to existing file")
                        .bindSelected(::appendMode)
                        .comment("Append to existing file instead of overwriting")
                }
                row {
                    checkBox("Open file after save")
                        .bindSelected(::openAfterSave)
                        .comment("Automatically open the output file in editor")
                }
            }
            
            group("Content Settings") {
                row {
                    checkBox("Include timestamp")
                        .bindSelected(::includeTimestamp)
                        .comment("Add timestamp to each file block")
                }
                row {
                    checkBox("Include file tree")
                        .bindSelected(::includeFileTree)
                        .comment("Include a file tree at the top of the output")
                }
                row {
                    checkBox("Include file analysis")
                        .bindSelected(::includeFileAnalysis)
                        .comment("Include file type and count analysis")
                }
                row {
                    checkBox("Compress content")
                        .bindSelected(::compressContent)
                        .comment("Remove whitespace and comments for supported languages")
                }
                row("Separator:") {
                    textField()
                        .bindText(::separator)
                        .comment("Separator string between files in output")
                }
            }
            
            group("Filtering Settings") {
                row("Filtered extensions:") {
                    textField()
                        .bindText(::filteredExtensions)
                        .comment("Comma-separated list of file extensions to exclude (e.g., .png, .jpg, .exe)")
                }
                row("Max file size (MB):") {
                    intTextField()
                        .bindIntText(::maxFileSize)
                        .comment("Maximum size in MB for files to include")
                }
                row {
                    checkBox("Use .gitignore")
                        .bindSelected(::useGitignore)
                        .comment("Respect .gitignore rules when selecting files")
                }
                row("Symlink handling:") {
                    comboBox(listOf("skip", "resolve"))
                        .bindItem(::symlinkHandling)
                        .comment("How to handle symbolic links: skip or resolve")
                }
            }
        }
        return component!!
    }

    override fun isModified(): Boolean {
        return zipFileName != settings.zipFileName ||
                outputFileName != settings.outputFileName ||
                appendMode != settings.appendMode ||
                includeTimestamp != settings.includeTimestamp ||
                filteredExtensions != settings.filteredExtensions.joinToString(", ") ||
                maxFileSize != (settings.maxFileSize / 1024 / 1024).toInt() ||
                outputSubfolder != settings.outputSubfolder ||
                openAfterSave != settings.openAfterSave ||
                separator != settings.separator ||
                useGitignore != settings.useGitignore ||
                historyFolder != settings.historyFolder ||
                includeFileTree != settings.includeFileTree ||
                includeFileAnalysis != settings.includeFileAnalysis ||
                symlinkHandling != settings.symlinkHandling ||
                compressContent != settings.compressContent
    }

    override fun apply() {
        settings.zipFileName = zipFileName
        settings.outputFileName = outputFileName
        settings.appendMode = appendMode
        settings.includeTimestamp = includeTimestamp
        settings.filteredExtensions = filteredExtensions.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        settings.maxFileSize = maxFileSize * 1024L * 1024L // Convert MB to bytes
        settings.outputSubfolder = outputSubfolder
        settings.openAfterSave = openAfterSave
        settings.separator = separator
        settings.useGitignore = useGitignore
        settings.historyFolder = historyFolder
        settings.includeFileTree = includeFileTree
        settings.includeFileAnalysis = includeFileAnalysis
        settings.symlinkHandling = symlinkHandling
        settings.compressContent = compressContent
    }

    override fun reset() {
        zipFileName = settings.zipFileName
        outputFileName = settings.outputFileName
        appendMode = settings.appendMode
        includeTimestamp = settings.includeTimestamp
        filteredExtensions = settings.filteredExtensions.joinToString(", ")
        maxFileSize = (settings.maxFileSize / 1024 / 1024).toInt()
        outputSubfolder = settings.outputSubfolder
        openAfterSave = settings.openAfterSave
        separator = settings.separator
        useGitignore = settings.useGitignore
        historyFolder = settings.historyFolder
        includeFileTree = settings.includeFileTree
        includeFileAnalysis = settings.includeFileAnalysis
        symlinkHandling = settings.symlinkHandling
        compressContent = settings.compressContent
        component?.reset()
    }
}