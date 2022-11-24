package me.santio.mhweb.common.adapter

import me.santio.mhweb.common.models.packets.TinyPlayer
import java.io.File

abstract class ServerAdapter {
    abstract fun executeCommand(command: String)
    abstract fun getOnlinePlayers(): List<TinyPlayer>

    abstract fun getPlugins(): List<ServerPlugin>
    abstract fun loadPlugin(plugin: File): ServerPlugin?
    abstract fun unloadPlugin(plugin: ServerPlugin)
}