package me.santio.mhweb.spigot

import me.santio.mhweb.common.Glass
import me.santio.mhweb.spigot.listeners.PlayerListeners
import me.santio.mhweb.spigot.logger.LogAppender
import me.santio.mhweb.spigot.tasks.BanlistCheckTask
import me.santio.mhweb.spigot.tasks.OpCheckTask
import me.santio.mhweb.spigot.tasks.WhitelistCheckTask
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class GlassSpigot : JavaPlugin(), Listener {

    companion object {
        fun getInstance() = getPlugin(GlassSpigot::class.java)

        var appender: LogAppender? = null
    }

    override fun onEnable() {
        saveDefaultConfig()
        Glass.setServerAdapter(GlassSpigotAdapter)

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
        val serverType = if (server.bukkitVersion.contains("Paper")
            || server.version.contains("Paper")) Glass.ServerType.PAPER
        else Glass.ServerType.SPIGOT

        // Login to glass
        Glass.setServerToken(config.getString("base_uri")!!,config.getString("token")!!, serverType)

        // Register listeners
        appender = LogAppender()
        server.pluginManager.registerEvents(PlayerListeners, this)

        // Start tasks
        server.scheduler.runTaskTimerAsynchronously(this, OpCheckTask, 0, 5)
        server.scheduler.runTaskTimerAsynchronously(this, BanlistCheckTask, 0, 5)
        server.scheduler.runTaskTimerAsynchronously(this, WhitelistCheckTask, 0, 5)
    }

    override fun onDisable() {
        Glass.close()
        appender?.close()
    }

}