package com.mohesu.combinecontext.actions

import com.intellij.openapi.vfs.VirtualFile

object LastActionStorage {
    private var lastActionType: String? = null
    private var lastSelectedFiles: Array<VirtualFile>? = null
    private var lastFileCount: Int = 0
    
    fun setLastAction(actionType: String, selectedFiles: Array<VirtualFile>, fileCount: Int) {
        lastActionType = actionType
        lastSelectedFiles = selectedFiles
        lastFileCount = fileCount
    }
    
    fun getLastAction(): Triple<String?, Array<VirtualFile>?, Int>? {
        return if (lastActionType != null && lastSelectedFiles != null) {
            Triple(lastActionType, lastSelectedFiles, lastFileCount)
        } else null
    }
    
    fun hasLastAction(): Boolean = lastActionType != null && lastSelectedFiles != null
}