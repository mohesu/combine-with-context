package com.mohesu.combinecontext.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class GitIgnoreFilter(private val patterns: List<String>) {
    
    fun isIgnored(relativePath: String): Boolean {
        val path = relativePath.replace('\\', '/')
        
        for (pattern in patterns) {
            if (pattern.isBlank() || pattern.startsWith("#")) continue
            
            var normalizedPattern = pattern.trim()
            if (normalizedPattern.startsWith("/")) {
                normalizedPattern = normalizedPattern.substring(1)
            }
            
            val isDirectory = normalizedPattern.endsWith("/")
            if (isDirectory) {
                normalizedPattern = normalizedPattern.removeSuffix("/")
            }
            
            // Simple glob matching - can be enhanced later
            val regex = normalizedPattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".")
            
            val fullRegex = if (normalizedPattern.contains("/")) {
                "^$regex$"
            } else {
                "(^|.*/)$regex(/.*)?$"
            }
            
            if (path.matches(Regex(fullRegex))) {
                return true
            }
            
            // Check if any parent directory matches
            val parts = path.split("/")
            for (i in parts.indices) {
                val parentPath = parts.take(i + 1).joinToString("/")
                if (parentPath.matches(Regex(fullRegex))) {
                    return true
                }
            }
        }
        
        return false
    }
    
    companion object {
        fun fromFile(workspaceRoot: String): GitIgnoreFilter? {
            val gitignoreFile = File(workspaceRoot, ".gitignore")
            if (!gitignoreFile.exists()) return null
            
            return try {
                val lines = gitignoreFile.readLines()
                GitIgnoreFilter(lines)
            } catch (e: Exception) {
                null
            }
        }
    }
}