package me.santio.mhweb.common.adapter

import me.santio.mhweb.common.models.packets.TinyPlayer
import java.io.File
import java.util.*

abstract class ServerAdapter {
    abstract fun executeCommand(command: String)

    abstract fun getOnlinePlayers(): List<TinyPlayer>
    abstract fun getWhitelistedPlayers(): List<TinyPlayer>
    abstract fun getServerAdministrators(): List<TinyPlayer>
    abstract fun getBannedPlayers(): List<TinyPlayer>

    abstract fun kickPlayer(uuid: UUID, reason: String): Boolean
    abstract fun banPlayer(uuid: UUID, reason: String): Boolean

    abstract fun getPlugins(): List<ServerPlugin>
    abstract fun loadPlugin(plugin: File): ServerPlugin?
    abstract fun unloadPlugin(plugin: ServerPlugin)
}