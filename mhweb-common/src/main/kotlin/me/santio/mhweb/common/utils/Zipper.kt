package me.santio.mhweb.common.utils

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import net.lingala.zip4j.ZipFile
import java.io.File
import java.util.*

object Zipper {

    fun zipFolder(source: File): File? {
        if (!source.exists()) return null
        val id = UUID.randomUUID().toString()

        val tempFile = GlassFileManager.fileFromPath("/.glass/$id.zip")
        tempFile.parentFile.mkdirs()

        val zip = ZipFile(tempFile)
        zip.addFolder(source)

        return tempFile
    }

}