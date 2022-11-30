package me.santio.mhweb.spigot

import me.santio.mhweb.common.adapter.ServerAdapter
import me.santio.mhweb.common.adapter.ServerPlugin
import me.santio.mhweb.common.models.packets.TinyPlayer
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*
import java.util.function.Consumer

object GlassSpigotAdapter: ServerAdapter() {

    fun OfflinePlayer.toTinyPlayer(): TinyPlayer {
        return TinyPlayer(
            name ?: "FETCH_FAILED",
            uniqueId.toString(),
            isOp,
            isWhitelisted,
            isOnline
        )
    }

    override fun executeCommand(command: String) {
        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance(), Consumer {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, command)
        })
    }

    override fun getOnlinePlayers(): List<TinyPlayer> {
        return Bukkit.getServer().onlinePlayers.map { it.toTinyPlayer() }
    }

    override fun loadPlugin(plugin: File): ServerPlugin? {
        val bukkitPlugin = Bukkit.getServer().pluginManager.loadPlugin(plugin) ?: return null
        return bukkitPlugin.toGlassPlugin()
    }

    override fun unloadPlugin(plugin: ServerPlugin) {
        Bukkit.getServer().pluginManager.disablePlugin(Bukkit.getServer().pluginManager.getPlugin(plugin.name)!!)
    }

    override fun getPlugins(): List<ServerPlugin> {
        return Bukkit.getServer().pluginManager.plugins.map { it.toGlassPlugin() }
    }

    override fun getWhitelistedPlayers(): List<TinyPlayer> {
        return Bukkit.getServer().whitelistedPlayers.map { it.toTinyPlayer() }
    }

    override fun getServerAdministrators(): List<TinyPlayer> {
        return Bukkit.getServer().operators.map { it.toTinyPlayer() }
    }

    @Suppress("DEPRECATION")
    override fun getBannedPlayers(): List<TinyPlayer> {
        return Bukkit.getServer()
            .getBanList(BanList.Type.NAME)
            .banEntries
            .map { Bukkit.getOfflinePlayer(it.target) }
            .map { it.toTinyPlayer() }
    }

    override fun kickPlayer(uuid: UUID, reason: String): Boolean {
        val player = Bukkit.getServer().getPlayer(uuid) ?: return false
        player.kickPlayer(reason)
        return true
    }

    override fun banPlayer(uuid: UUID, reason: String): Boolean {
        val player = Bukkit.getServer().getPlayer(uuid) ?: return false
        Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(player.name, reason, null, null)
        player.kickPlayer(reason)
        return true
    }
}