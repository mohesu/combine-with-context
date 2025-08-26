package com.mohesu.combinecontext.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

object FileUtils {
    
    fun isBinaryFile(content: ByteArray): Boolean {
        if (content.isEmpty()) return false
        for (i in 0 until min(content.size, 8192)) {
            if (content[i] == 0.toByte()) return true
        }
        return false
    }
    
    fun ensureDirExists(dirPath: String) {
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }
    
    fun timestampString(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd'T'HH_mm_ss"))
    }
    
    fun stripControlChars(text: String): String {
        return text.replace(Regex("[\u0009\u000B\u000C\u000E-\u001F\u007F\u009F]"), "")
    }
    
    fun getBestFence(content: String): String {
        var max = 3
        val regex = Regex("(`{3,})")
        regex.findAll(content).forEach { match ->
            if (match.groupValues[1].length >= max) {
                max = match.groupValues[1].length + 1
            }
        }
        return "`".repeat(max)
    }
    
    fun buildFileTree(relPaths: List<String>): String {
        data class Node(val children: MutableMap<String, Node> = mutableMapOf())
        
        val root = Node()
        
        for (relPath in relPaths) {
            val parts = relPath.split("/")
            var current = root
            for (part in parts) {
                if (!current.children.containsKey(part)) {
                    current.children[part] = Node()
                }
                current = current.children[part]!!
            }
        }
        
        val lines = mutableListOf<String>()
        
        fun buildLines(node: Node, prefix: String) {
            val entries = node.children.entries.sortedBy { it.key }
            for ((index, entry) in entries.withIndex()) {
                val (name, child) = entry
                val isLast = index == entries.size - 1
                val connector = if (isLast) "└── " else "├── "
                val suffix = if (child.children.isNotEmpty()) "/" else ""
                lines.add("$prefix$connector$name$suffix")
                
                if (child.children.isNotEmpty()) {
                    val childPrefix = prefix + if (isLast) "    " else "│   "
                    buildLines(child, childPrefix)
                }
            }
        }
        
        buildLines(root, "")
        return lines.joinToString("\n")
    }
    
    fun getMarkdownLangForFile(fileName: String, markdownMapping: Map<String, String>): String {
        val extension = File(fileName).extension.lowercase()
        if (extension.isEmpty()) return ""
        return markdownMapping[".$extension"] ?: ""
    }
    
    fun isEscaped(code: String, index: Int): Boolean {
        var backslashes = 0
        for (i in index - 1 downTo 0) {
            if (code[i] == '\\') backslashes++
            else break
        }
        return backslashes % 2 == 1
    }
    
    fun removeCommentsJS(code: String): String {
        val output = StringBuilder()
        var i = 0
        var inString = false
        var stringChar = ' '
        var inRegex = false
        var inCommentSingle = false
        var inCommentMulti = false
        
        while (i < code.length) {
            val c = code[i]
            
            if (inCommentSingle) {
                if (c == '\n') inCommentSingle = false
                i++
                continue
            }
            
            if (inCommentMulti) {
                if (c == '*' && i + 1 < code.length && code[i + 1] == '/') {
                    i += 2
                    inCommentMulti = false
                    continue
                }
                i++
                continue
            }
            
            if (inString) {
                output.append(c)
                if (c == stringChar && !isEscaped(code, i)) {
                    inString = false
                }
                i++
                continue
            }
            
            if (inRegex) {
                output.append(c)
                if (c == '/' && !isEscaped(code, i)) {
                    inRegex = false
                }
                i++
                continue
            }
            
            if (c == '"' || c == '\'' || c == '`') {
                inString = true
                stringChar = c
                output.append(c)
                i++
                continue
            }
            
            if (c == '/' && i + 1 < code.length) {
                val next = code[i + 1]
                if (next == '/') {
                    inCommentSingle = true
                    i += 2
                    continue
                }
                if (next == '*') {
                    inCommentMulti = true
                    i += 2
                    continue
                }
                inRegex = true
                output.append(c)
                i++
                continue
            }
            
            output.append(c)
            i++
        }
        
        return output.toString()
    }
    
    fun removeCommentsPython(code: String): String {
        val output = StringBuilder()
        var i = 0
        var inString = false
        var stringChar = ' '
        var isTriple = false
        
        while (i < code.length) {
            val c = code[i]
            
            if (inString) {
                output.append(c)
                if (isTriple) {
                    if (c == stringChar &&
                        i + 2 < code.length &&
                        code[i + 1] == stringChar &&
                        code[i + 2] == stringChar &&
                        !isEscaped(code, i)) {
                        inString = false
                        output.append(stringChar).append(stringChar)
                        i += 3
                        continue
                    }
                } else {
                    if (c == stringChar && !isEscaped(code, i)) {
                        inString = false
                    }
                }
                i++
                continue
            }
            
            if (c == '#') {
                while (i < code.length && code[i] != '\n') {
                    i++
                }
                continue
            }
            
            if (c == '"' || c == '\'') {
                inString = true
                stringChar = c
                isTriple = (i + 2 < code.length && code[i + 1] == c && code[i + 2] == c)
                output.append(c)
                i++
                if (isTriple) {
                    output.append(c).append(c)
                    i += 2
                }
                continue
            }
            
            output.append(c)
            i++
        }
        
        return output.toString()
    }
    
    fun removeCommentsCSS(code: String): String {
        return code.replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
    }
    
    private val indentDependentLangs = setOf("python", "yaml")
    
    fun minifyContent(content: String, lang: String): String {
        var cleaned = content
        val lowerLang = (lang).lowercase()
        
        when (lowerLang) {
            "javascript", "typescript" -> cleaned = removeCommentsJS(content)
            "python" -> cleaned = removeCommentsPython(content)
            "css" -> cleaned = removeCommentsCSS(content)
        }
        
        var lines = cleaned.split("\n")
        
        if (indentDependentLangs.contains(lowerLang)) {
            lines = lines.map { it.trimEnd() }
        } else {
            lines = lines.map { it.trim().replace(Regex("\\s+"), " ") }
        }
        
        lines = lines.filter { it.isNotEmpty() }
        return lines.joinToString("\n")
    }
}