package me.santio.glass.common

import me.santio.glass.common.adapter.ServerPlugin
import java.io.File

object GlassPluginManager {

    val pluginFolder: File = File("${me.santio.glass.common.GlassFileManager.HOME_DIR}/plugins")

    fun getPluginFile(name: String): File {
        val fullName = if (name.endsWith(".jar")) name else "$name.jar"
        return File(me.santio.glass.common.GlassPluginManager.pluginFolder, fullName)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getPlugins(): List<me.santio.glass.common.adapter.ServerPlugin> {
        return me.santio.glass.common.Glass.server.getPlugins()
    }

    fun getPlugin(name: String): me.santio.glass.common.adapter.ServerPlugin? {
        return me.santio.glass.common.GlassPluginManager.getPlugins().firstOrNull { it.name == name }
    }

    fun loadPlugin(plugin: File): me.santio.glass.common.adapter.ServerPlugin? {
        return me.santio.glass.common.Glass.server.loadPlugin(plugin)
    }

    fun unloadPlugin(plugin: me.santio.glass.common.adapter.ServerPlugin) {
        me.santio.glass.common.Glass.server.unloadPlugin(plugin)
    }

}