package me.santio.mhweb.common

import io.socket.client.Ack
import io.socket.emitter.Emitter
import me.santio.mhweb.common.models.packets.FileData
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketHandler
import me.santio.mhweb.common.utils.Zipper
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

// Presents a file manager wrapper for the server.

@Suppress("MemberVisibilityCanBePrivate", "unused")
object GlassFileManager {

    private val LOCKED_DIRECTORIES: Set<String> = setOf("/__resources", "/plugins/Glass", "/plugins/MHWeb")
    private val HOME_DIR: String = System.getenv("ROOT_SERVER") ?: System.getenv("HOME") ?: "/"
    private val UPLOADING: MutableMap<String, FileLocation> = mutableMapOf()

    fun fileFromPath(path: String, root: Boolean = false): File {
        var newPath = path
        if (!path.startsWith("/")) newPath = "/$path"
        if (root) return File(path)

        if (path.startsWith(HOME_DIR)) newPath = path.substring(HOME_DIR.length)
        newPath = HOME_DIR + newPath

        return File(newPath)
    }

    fun fetchFile(path: String, minimal: Boolean = false, absolute: Boolean = false, root: Boolean = false): FileData {
        val file = fileFromPath(path, root = root)
        if (!file.exists()) return FileData(path, directory = false, accessible = false, error = "File does not exist")

        try {
            val size = Files.size(file.toPath())
            val directory = file.isDirectory
            val name = file.name
            val fileData = FileData(
                if (absolute) file.absolutePath.substring(HOME_DIR.length) else name,
                directory,
                true,
                size
            )
            if (minimal) return fileData

            val cleanPath = (if (HOME_DIR.endsWith("/")) HOME_DIR.substring(0, HOME_DIR.length-1) else HOME_DIR) + path
            if (directory && LOCKED_DIRECTORIES.contains(cleanPath)) {
                fileData.accessible = false
                fileData.error = "You are not permitted to access this resource!"
                return fileData
            }

            if (!directory) {
                if (size > 3 * 1024 * 1024) {
                    fileData.content = "File is too large to be displayed"
                } else {
                    fileData.content = file.readText()
                }
            } else {
                val children: MutableList<FileData> = mutableListOf()
                file.listFiles()?.forEach { children.add(fetchFile(it.absolutePath, true)) }
                fileData.children = children
            }

            return fileData
        } catch(e: SecurityException) {
            return FileData(file.name, directory = false, accessible = false, error = "You are not permitted to access this resource!")
        } catch(e: IOException) {
            return FileData(file.name, directory = false, accessible = false, error = "An error occurred while reading the file metadata!")
        }
    }

    fun fetchAllFiles(path: String, root: Boolean = false): List<FileData> {
        val homePath = fileFromPath(path, root).absolutePath

        val files: MutableList<FileData> = mutableListOf()
        val home = File(homePath)
        home.walk().forEach {
            files.add(fetchFile(it.absolutePath, minimal = true, absolute = true))
        }

        return files
    }

    fun createFile(location: FileLocation) {
        val file = location.getFile()
        if (file.exists()) return

        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()
    }

    fun downloadFile(location: FileLocation, acknowledgement: Ack) {
        val file = location.getFile()
        if (!file.exists()) return

        val id = UUID.randomUUID().toString()
        acknowledgement.call(id)

        // Prefixed since this is not a trusted source
        val room = "download-$id"

        if (file.isDirectory) {
            val zip = Zipper.zipFolder(file)
            if (zip != null) {
                SocketHandler.sendFile(room, zip) {
                    zip.delete()
                }
            }
        }
        else SocketHandler.sendFile(room, file)
    }

    fun uploadFile(location: FileLocation, id: String) {
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

    fun deleteFile(location: FileLocation) {
        val file = location.getFile()
        if (!file.exists()) return

        if (file.isDirectory) file.deleteRecursively()
        else file.delete()
    }

    fun createDirectory(location: FileLocation): String? {
        val file = location.getFile()
        if (file.exists()) return null

        file.mkdirs()
        return location.getTopDirectory()
    }

    fun moveFile(previous: FileLocation, next: FileLocation) {
        val previousFile = previous.getFile()
        val nextFile = next.getFile()
        if (!previousFile.exists()) return

        previousFile.renameTo(nextFile)
    }

}