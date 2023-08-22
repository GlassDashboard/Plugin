package me.santio.glass.spigot

import me.santio.mhweb.common.Glass
import me.santio.glass.spigot.listeners.PlayerListeners
import me.santio.glass.spigot.logger.LogAppender
import me.santio.glass.spigot.tasks.BanlistCheckTask
import me.santio.glass.spigot.tasks.OpCheckTask
import me.santio.glass.spigot.tasks.WhitelistCheckTask
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

        // Determine server type
        val serverType = Glass.ServerType
            .fromRoot(Glass.ServerType.Root.BUKKIT)
            .find { isServerType(it) }
            ?: Glass.ServerType.SPIGOT

        // Login to glass
        Glass.setServerToken(config.getString("base_uri")!!, config.getString("token")!!, serverType)

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

    private fun isServerType(type: Glass.ServerType): Boolean {
        return server.bukkitVersion.contains(type.name, true) || server.version.contains(type.name, true)
    }

}