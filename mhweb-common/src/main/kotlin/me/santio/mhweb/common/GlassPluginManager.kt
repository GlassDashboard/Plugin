package me.santio.mhweb.common

import me.santio.mhweb.common.adapter.ServerPlugin
import java.io.File

object GlassPluginManager {

    val pluginFolder: File = File("${GlassFileManager.HOME_DIR}/plugins")

    fun getPluginFile(name: String): File {
        val fullName = if (name.endsWith(".jar")) name else "$name.jar"
        return File(pluginFolder, fullName)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getPlugins(): List<ServerPlugin> {
        return Glass.server.getPlugins()
    }

    fun getPlugin(name: String): ServerPlugin? {
        return getPlugins().firstOrNull { it.name == name }
    }

    fun loadPlugin(plugin: File): ServerPlugin? {
        return Glass.server.loadPlugin(plugin)
    }

    fun unloadPlugin(plugin: ServerPlugin) {
        Glass.server.unloadPlugin(plugin)
    }

}