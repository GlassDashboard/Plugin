package me.santio.glass.common.adapter

import me.santio.glass.common.models.packets.TinyPlayer
import java.io.File
import java.util.*

interface ServerAdapter {
    fun executeCommand(command: String)
    fun getServerVersion(): String
    fun getPluginVersion(): String

    fun getOnlinePlayers(): List<TinyPlayer>
    fun getWhitelistedPlayers(): List<TinyPlayer>
    fun getServerAdministrators(): List<TinyPlayer>
    fun getBannedPlayers(): List<TinyPlayer>

    fun kickPlayer(uuid: UUID, reason: String): Boolean
    fun banPlayer(uuid: UUID, reason: String): Boolean
    fun pardonPlayer(uuid: UUID): Boolean
    fun setWhitelisted(uuid: UUID, state: Boolean): Boolean
    fun setAdministrator(uuid: UUID, state: Boolean): Boolean

    fun getPlugins(): List<ServerPlugin>
    fun loadPlugin(plugin: File): ServerPlugin?
    fun unloadPlugin(plugin: ServerPlugin)
}