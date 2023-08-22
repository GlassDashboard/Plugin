package me.santio.glass.common

import io.socket.client.Ack
import io.socket.emitter.Emitter
import me.santio.glass.common.models.packets.FileMetadata
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketHandler
import me.santio.glass.common.utils.Zipper
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString

// Presents a file manager wrapper for the server.

@Suppress("MemberVisibilityCanBePrivate", "unused")
object GlassFileManager {

    private val LOCKED_DIRECTORIES: Set<String> = setOf("/__resources", "/plugins/Glass", "/plugins/MHWeb")
    private val UPLOADING: MutableMap<String, me.santio.glass.common.models.packets.ResolvablePath> = mutableMapOf()
    val HOME_DIR: String = Path.of("").absolutePathString()
    val DB_FILE = File("${me.santio.glass.common.GlassFileManager.HOME_DIR}/.glass/data.db")

    /**
     * Creates a simple metadata object from a file. This will not
     * propagate the children.
     * @param file The file to create the metadata from.
     * @return The metadata object.
     */
    private fun composeMetadata(file: File): me.santio.glass.common.models.packets.FileMetadata? {
        val size = if (file.isDirectory) -1 else file.length()
        val content: String? = if (file.isDirectory) null else if (size < 3 * 1024 * 1024) {
            file.readText()
        } else "File is too large to read."

        return try {
            me.santio.glass.common.models.packets.FileMetadata(
                file.name,
                file.absolutePath.removePrefix(me.santio.glass.common.GlassFileManager.HOME_DIR).ifBlank { "/" },
                file.isDirectory,
                size,
                file.lastModified(),
                content,
                listOf()
            )
        } catch (e: IOException) {
            null
        }
    }

    /**
     * Gets the metadata of a file.
     * @param path The path to the file.
     * @param recursive Whether to get the children of the children.
     * @return The metadata object.
     */
    @JvmOverloads
    fun getFileMetadata(path: me.santio.glass.common.models.packets.ResolvablePath, recursive: Boolean = false): me.santio.glass.common.models.packets.FileMetadata? {
        val file = path.getFile()
        if (!file.exists()) return null

        val children = if (file.isDirectory) {

            if (recursive) {
                file.listFiles()?.mapNotNull {
                    me.santio.glass.common.GlassFileManager.getFileMetadata(
                        me.santio.glass.common.GlassFileManager.absoluteToResolvable(
                            it.absolutePath
                        )
                    )
                }
            } else {
                file.listFiles()?.mapNotNull { me.santio.glass.common.GlassFileManager.composeMetadata(it) }
            } ?: listOf()

        } else listOf()

        return me.santio.glass.common.GlassFileManager.composeMetadata(file)?.copy(children = children)
    }

    /**
     * Converts a resolvable oath data to a file object.
     * @param path The path to the file.
     * @param root Whether to use absolute pathing.
     * @return A java file object.
     */
    fun fileFromPath(path: String, root: Boolean = false): File {
        var newPath = path
        if (!path.startsWith("/")) newPath = "/$path"
        if (root) return File(path)

        if (path.startsWith(me.santio.glass.common.GlassFileManager.HOME_DIR)) newPath = path.substring(me.santio.glass.common.GlassFileManager.HOME_DIR.length)
        newPath = me.santio.glass.common.GlassFileManager.HOME_DIR + newPath

        return File(newPath)
    }

    /**
     * Takes in an absolute path and converts it to a resolvable path. Used to assist in
     * making the code easier to work with.
     * @param path The absolute path.
     * @return The resolvable path.
     */
    fun absoluteToResolvable(path: String): me.santio.glass.common.models.packets.ResolvablePath {
        if (path.startsWith(me.santio.glass.common.GlassFileManager.HOME_DIR)) return me.santio.glass.common.models.packets.ResolvablePath(
            path.substring(me.santio.glass.common.GlassFileManager.HOME_DIR.length)
        )
        return me.santio.glass.common.models.packets.ResolvablePath(path)
    }

    fun createFile(location: me.santio.glass.common.models.packets.ResolvablePath) {
        val file = location.getFile()
        if (file.exists()) return

        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()
    }

    fun writeFile(location: me.santio.glass.common.models.packets.ResolvablePath, content: String) {
        val file = location.getFile()
        if (!file.exists()) return

        file.writeText(content)
    }

    fun downloadFile(location: me.santio.glass.common.models.packets.ResolvablePath, acknowledgement: Ack) {
        val file = location.getFile()
        if (!file.exists()) return

        // Prefixed since this is not a trusted source
        val id = UUID.randomUUID().toString()
        val room = "download-$id"

        if (file.isDirectory) {
            val zip = me.santio.glass.common.utils.Zipper.zipFolder(file)
            if (zip != null) {
                acknowledgement.call(id, zip.length())
                me.santio.glass.common.socket.SocketHandler.sendFile(room, zip) {
                    zip.delete()
                }
            }
        } else {
            me.santio.glass.common.socket.SocketHandler.sendFile(room, file)
            acknowledgement.call(id, file.length())
        }
    }

    fun uploadFile(location: me.santio.glass.common.models.packets.ResolvablePath, id: String) {
        val file = location.getFile()
        if (!file.exists()) {
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            file.createNewFile()
        }

        // id provided here is a 'trusted' source, so we don't need to encode the command
        me.santio.glass.common.GlassFileManager.UPLOADING[id] = location
        val stream = file.outputStream()

        val listener = Emitter.Listener { args ->
            val chunk = args[0] as ByteArray
            stream.write(chunk)
        }

        me.santio.glass.common.Glass.socket?.on("BUFFER-$id", listener)

        me.santio.glass.common.Glass.socket?.once("EOF-$id") {
            stream.close()
            me.santio.glass.common.Glass.socket?.off("BUFFER-$id", listener)
            me.santio.glass.common.GlassFileManager.UPLOADING.remove(id)
        }
    }

    fun deleteFile(location: me.santio.glass.common.models.packets.ResolvablePath) {
        val file = location.getFile()
        if (!file.exists()) return

        if (file.isDirectory) file.deleteRecursively()
        else file.delete()
    }

    fun createDirectory(location: me.santio.glass.common.models.packets.ResolvablePath): String? {
        val file = location.getFile()
        if (file.exists()) return null

        file.mkdirs()
        return location.getTopDirectory()
    }

    fun moveFile(previous: me.santio.glass.common.models.packets.ResolvablePath, next: me.santio.glass.common.models.packets.ResolvablePath) {
        val previousFile = previous.getFile()
        val nextFile = next.getFile()
        if (!previousFile.exists()) return

        if (!nextFile.parentFile.exists()) nextFile.parentFile.mkdirs()
        previousFile.renameTo(nextFile)
    }

    fun copyFile(previous: me.santio.glass.common.models.packets.ResolvablePath, to: me.santio.glass.common.models.packets.ResolvablePath) {
        val previousFile = previous.getFile()
        val nextFile = to.getFile()
        if (!previousFile.exists()) return

        if (!nextFile.parentFile.exists()) nextFile.parentFile.mkdirs()
        previousFile.copyTo(nextFile)
    }

    fun unarchive(path: me.santio.glass.common.models.packets.ResolvablePath) {
        val file = path.getFile()
        if (!file.exists()) return

        me.santio.glass.common.utils.Zipper.unzip(file)
    }

}