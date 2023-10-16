package me.santio.glass.spigot

import kong.unirest.core.Unirest
import me.santio.glass.common.adapter.ServerAdapter
import me.santio.glass.common.adapter.ServerPlugin
import me.santio.glass.common.models.packets.TinyPlayer
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.File
import java.util.*

object GlassSpigotAdapter: ServerAdapter {

    fun OfflinePlayer.toTinyPlayer(): TinyPlayer {
        return TinyPlayer(
            name ?: name(),
            uniqueId.toString(),
            isOp,
            isWhitelisted,
            isOnline
        )
    }

    fun OfflinePlayer.name(): String {
        return Unirest.get("https://api.ashcon.app/mojang/v2/user/${uniqueId}")
            .asJson()
            .body
            .getObject()
            .getString("username")
    }

    override fun executeCommand(command: String) {
        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, command)
        }
    }

    override fun getServerVersion(): String {
        return Bukkit.getServer().bukkitVersion.split("-")[0]
    }

    override fun getPluginVersion(): String {
        return GlassSpigot.getInstance().description.version
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
        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            player.kickPlayer(reason)
        }
        return true
    }

    override fun banPlayer(uuid: UUID, reason: String): Boolean {
        val player = Bukkit.getServer().getOfflinePlayer(uuid)

        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            val name = player.name()
            Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(name, reason, null, "Glass")

            if (player.isOnline) (Bukkit.getPlayer(uuid))!!.kickPlayer(reason)
        }

        return true
    }

    override fun pardonPlayer(uuid: UUID): Boolean {
        val player = Bukkit.getServer().getOfflinePlayer(uuid)
        val name = player.name ?: return false

        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(name)
        }

        return true
    }

    override fun setWhitelisted(uuid: UUID, state: Boolean): Boolean {
        val player = Bukkit.getServer().getOfflinePlayer(uuid)
        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            player.isWhitelisted = state
        }
        return true
    }

    override fun setAdministrator(uuid: UUID, state: Boolean): Boolean {
        val player = Bukkit.getServer().getOfflinePlayer(uuid)
        Bukkit.getServer().scheduler.runTask(GlassSpigot.getInstance()) {
            player.isOp = state
        }
        return true
    }
}