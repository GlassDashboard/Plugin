package me.santio.glass.common

import java.io.File

object GlassPluginManager {

    val pluginFolder: File = File("${GlassFileManager.HOME_DIR}/plugins")

    fun getPluginFile(name: String): File {
        val fullName = if (name.endsWith(".jar")) name else "$name.jar"
        return File(pluginFolder, fullName)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getPlugins(): List<me.santio.glass.common.adapter.ServerPlugin> {
        return Glass.server.getPlugins()
    }

    fun getPlugin(name: String): me.santio.glass.common.adapter.ServerPlugin? {
        return getPlugins().firstOrNull { it.name == name }
    }

    fun loadPlugin(plugin: File): me.santio.glass.common.adapter.ServerPlugin? {
        return Glass.server.loadPlugin(plugin)
    }

    fun unloadPlugin(plugin: me.santio.glass.common.adapter.ServerPlugin) {
        Glass.server.unloadPlugin(plugin)
    }

}