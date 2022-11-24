package me.santio.mhweb.spigot

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.adapter.ServerAdapter
import me.santio.mhweb.common.adapter.ServerPlugin
import me.santio.mhweb.common.models.packets.TinyPlayer
import me.santio.mhweb.spigot.logger.LogAppender
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.function.Consumer

class GlassSpigot : JavaPlugin(), Listener {

    companion object {
        var appender: LogAppender? = null
    }

    override fun onEnable() {
        saveDefaultConfig()

        Glass.setServerAdapter(object : ServerAdapter() {
            override fun executeCommand(command: String) {
                server.scheduler.runTask(this@GlassSpigot, Consumer {
                    server.dispatchCommand(server.consoleSender, command)
                })
            }

            override fun getOnlinePlayers(): List<TinyPlayer> {
                return server.onlinePlayers.map {
                    TinyPlayer(it.name, it.uniqueId.toString(), it.isOp)
                }
            }

            override fun loadPlugin(plugin: File): ServerPlugin? {
                val bukkitPlugin = server.pluginManager.loadPlugin(plugin) ?: return null
                return bukkitPlugin.toGlassPlugin()
            }

            override fun unloadPlugin(plugin: ServerPlugin) {
                server.pluginManager.disablePlugin(server.pluginManager.getPlugin(plugin.name)!!)
            }

            override fun getPlugins(): List<ServerPlugin> {
                return server.pluginManager.plugins.map { it.toGlassPlugin() }
            }
        })

        if (!config.contains("base_uri")) {
            Glass.log("Invalid base_uri provided, please repair your config.yml")
            isEnabled = false
            return
        }

        if (!config.contains("token")) {
            Glass.log("Please enter your token in the config.yml in /plugins/Glass/config.yml")
            isEnabled = false
            return
        }

        // Check if the server is a spigot or paper server
        val serverType = if (server.bukkitVersion.contains("Paper")) Glass.ServerType.PAPER
        else Glass.ServerType.SPIGOT

        Glass.setServerToken(config.getString("base_uri")!!,config.getString("token")!!, serverType)
        appender = LogAppender()

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        Glass.close()
        appender?.close()
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        Glass.updatePlayerList()
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskLater(this, Runnable {
            Glass.updatePlayerList()
        }, 1)
    }

}