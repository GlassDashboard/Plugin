package me.santio.glass.common.adapter

import me.santio.glass.common.Glass
import java.io.File

data class ServerPlugin(
    private val file: File,
    val name: String,
    val version: String,
    val description: String,
    val authors: List<String>,
    val enabled: Boolean
) {

    fun load(): me.santio.glass.common.adapter.ServerPlugin {
        me.santio.glass.common.Glass.server.loadPlugin(file)
        return this
    }

    fun unload(): me.santio.glass.common.adapter.ServerPlugin {
        me.santio.glass.common.Glass.server.unloadPlugin(this)
        return this
    }

}
