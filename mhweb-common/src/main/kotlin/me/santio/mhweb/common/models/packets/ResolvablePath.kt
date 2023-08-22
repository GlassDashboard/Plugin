@file:Suppress("MemberVisibilityCanBePrivate")

package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable
import me.santio.mhweb.common.GlassFileManager
import java.io.File

@Serializable
data class ResolvablePath (
    val path: String,
    val root: Boolean = false
) {

    fun getFile(): File {
        return GlassFileManager.fileFromPath(path, root)
    }

    fun isDirectoryRecursive(): Boolean {
        return path.contains("/")
    }

    fun getTopDirectory(): String? {
        if (!isDirectoryRecursive()) return null
        return path.split("/").firstOrNull()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ResolvablePath) return false
        return other.getFile().absolutePath == getFile().absolutePath
    }
}