package me.santio.glass.common.adapter

import me.santio.glass.common.models.packets.TinyPlayer
import java.io.File
import java.util.*

interface ServerAdapter {
    fun executeCommand(command: String)
    fun getServerVersion(): String
    fun getPluginVersion(): String

    fun getOnlinePlayers(): List<me.santio.glass.common.models.packets.TinyPlayer>
    fun getWhitelistedPlayers(): List<me.santio.glass.common.models.packets.TinyPlayer>
    fun getServerAdministrators(): List<me.santio.glass.common.models.packets.TinyPlayer>
    fun getBannedPlayers(): List<me.santio.glass.common.models.packets.TinyPlayer>

    fun kickPlayer(uuid: UUID, reason: String): Boolean
    fun banPlayer(uuid: UUID, reason: String): Boolean
    fun pardonPlayer(uuid: UUID): Boolean
    fun setWhitelisted(uuid: UUID, state: Boolean): Boolean
    fun setAdministrator(uuid: UUID, state: Boolean): Boolean

    fun getPlugins(): List<me.santio.glass.common.adapter.ServerPlugin>
    fun loadPlugin(plugin: File): me.santio.glass.common.adapter.ServerPlugin?
    fun unloadPlugin(plugin: me.santio.glass.common.adapter.ServerPlugin)
}