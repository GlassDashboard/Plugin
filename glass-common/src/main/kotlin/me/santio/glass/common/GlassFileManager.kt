package me.santio.glass.common

import io.socket.emitter.Emitter
import kong.unirest.core.Unirest
import me.santio.glass.common.models.packets.FileMetadata
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.utils.Zipper
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.util.function.Consumer
import kotlin.io.path.absolutePathString

// Presents a file manager wrapper for the server.
@Suppress("MemberVisibilityCanBePrivate", "unused")
object GlassFileManager {

    private val LOCKED_DIRECTORIES: Set<String> = setOf("/__resources", "/plugins/Glass", "/plugins/MHWeb", "/.glass")
    private val UNREADABLE_EXT: Set<String> = setOf("jar", "zip", "exe", "db", "dat", "dll", "gz", "png")
    private val UPLOADING: MutableMap<String, ResolvablePath> = mutableMapOf()

    val HOME_DIR: String = Path.of("").absolutePathString()
    val DB_FILE = File("$HOME_DIR/.glass/data.db")

    /**
     * Creates a simple metadata object from a file. This will not
     * propagate the children.
     * @param file The file to create the metadata from.
     * @return The metadata object.
     */
    private fun composeMetadata(file: File): FileMetadata? {
        val size = if (file.isDirectory) -1 else file.length()
        val shouldRead = !UNREADABLE_EXT.contains(file.extension) && size < 2 * 1024 * 1024

        val content: String? = if (file.isDirectory) null else if (shouldRead) {
            file.readText()
        } else "File is too large to read."

        return try {
            FileMetadata(
                file.name,
                file.relativePath(),
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
    fun getFileMetadata(
        path: ResolvablePath,
        recursive: Boolean = false
    ): FileMetadata? {
        val file = path.getFile()
        if (!file.exists()) return null

        val children = if (file.isDirectory) {

            if (recursive) {
                file.listPublicFiles().mapNotNull {
                    getFileMetadata(
                        absoluteToResolvable(
                            it.absolutePath
                        )
                    )
                }
            } else {
                file.listPublicFiles().mapNotNull { composeMetadata(it) }
            }

        } else listOf()

        return composeMetadata(file)?.copy(children = children)
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

        if (path.startsWith(HOME_DIR)) newPath =
            path.substring(HOME_DIR.length)
        newPath = HOME_DIR + newPath

        return File(newPath)
    }

    /**
     * Takes in an absolute path and converts it to a resolvable path. Used to assist in
     * making the code easier to work with.
     * @param path The absolute path.
     * @return The resolvable path.
     */
    fun absoluteToResolvable(path: String): ResolvablePath {
        if (path.startsWith(HOME_DIR)) return ResolvablePath(
            path.substring(HOME_DIR.length)
        )
        return ResolvablePath(path)
    }

    /**
     * Create an empty file at the given location, and create the parent directories if they don't exist.
     * @param location The location to create the file.
     */
    fun createFile(location: ResolvablePath) {
        val file = location.getFile()
        if (file.exists()) return

        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()
    }

    /**
     * Write the given content to the file at the given location.
     * @param location The location of the file.
     * @param content The content to write.
     */
    fun writeFile(location: ResolvablePath, content: String) {
        val file = location.getFile()
        if (!file.exists()) return

        file.writeText(content)
    }

    /**
     * Download a file from the given url to the given location.
     * @param location The location to download the file to.
     * @param url The url to download the file from.
     * @param acknowledgement The acknowledgement callback, containing if the download was successful or not.
     */
    fun downloadFile(location: File, url: URI, acknowledgement: Consumer<Boolean>) {
        if (location.exists()) location.deleteRecursively()
        if (!location.parentFile.exists()) location.parentFile.mkdirs()

        Unirest.get(url.toString())
            .asFile(location.absolutePath)
            .ifSuccess { acknowledgement.accept(true) }
            .ifFailure {
                acknowledgement.accept(false)
                println(it.statusText)
            }
    }

    fun uploadFile(location: ResolvablePath, id: String) {
        val file = location.getFile()
        if (!file.exists()) {
            if (!file.parentFile.exists()) file.parentFile.mkdirs()
            file.createNewFile()
        }

        // id provided here is a 'trusted' source, so we don't need to encode the command
        UPLOADING[id] = location
        val stream = file.outputStream()

        val listener = Emitter.Listener { args ->
            val chunk = args[0] as ByteArray
            stream.write(chunk)
        }

        Glass.socket?.on("BUFFER-$id", listener)

        Glass.socket?.once("EOF-$id") {
            stream.close()
            Glass.socket?.off("BUFFER-$id", listener)
            UPLOADING.remove(id)
        }
    }

    /**
     * Delete a file at the given location.
     * @param location The location of the file.
     */
    fun deleteFile(location: ResolvablePath) {
        val file = location.getFile()
        if (!file.exists()) return

        if (file.isDirectory) file.deleteRecursively()
        else file.delete()
    }

    /**
     * Create a directory at the given location.
     * @param location The location to create the directory.
     * @return The top directory of the created directory.
     */
    fun createDirectory(location: ResolvablePath): String? {
        val file = location.getFile()
        if (file.exists()) return null

        file.mkdirs()
        return location.getTopDirectory()
    }

    /**
     * Move a file from the previous location to the next location.
     * @param previous The previous location of the file.
     * @param next The next location of the file.
     */
    fun moveFile(
        previous: ResolvablePath,
        next: ResolvablePath
    ) {
        val previousFile = previous.getFile()
        val nextFile = next.getFile()
        if (!previousFile.exists()) return

        if (!nextFile.parentFile.exists()) nextFile.parentFile.mkdirs()
        previousFile.renameTo(nextFile)
    }

    /**
     * Copy a file from the previous location to the next location.
     * @param previous The previous location of the file.
     * @param to The next location of the file.
     */
    fun copyFile(
        previous: ResolvablePath,
        to: ResolvablePath
    ) {
        val previousFile = previous.getFile()
        val nextFile = to.getFile()
        if (!previousFile.exists()) return

        if (!nextFile.parentFile.exists()) nextFile.parentFile.mkdirs()
        previousFile.copyTo(nextFile)
    }

    /**
     * Unarchive a .zip file at the given location.
     */
    fun unarchive(path: ResolvablePath) {
        val file = path.getFile()
        if (!file.exists()) return

        Zipper.unzip(file)
    }

    // Extensions
    fun File.relativePath(): String {
        return this.absolutePath.removePrefix(HOME_DIR).ifBlank { "/" }
    }

    fun File.listPublicFiles(): List<File> {
        return this.listFiles()?.filter {
            it.relativePath() !in LOCKED_DIRECTORIES
        } ?: listOf()
    }

}