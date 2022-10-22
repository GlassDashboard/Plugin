@file:Suppress("MemberVisibilityCanBePrivate")

package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable
import me.santio.mhweb.common.GlassFileManager
import java.io.File

@Serializable
data class FileLocation (
    val path: String,
    val root: Boolean
) {

    fun getFile(): File {
        return GlassFileManager.fileFromPath(path, root)
    }

    // Used for FTP
    fun isDirectoryRecursive(): Boolean {
        return path.contains("/")
    }

    fun getTopDirectory(): String? {
        if (!isDirectoryRecursive()) return null
        return path.split("/").firstOrNull()
    }

}