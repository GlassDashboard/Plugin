package me.santio.mhweb.spigot.listeners

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.TrackedCount
import me.santio.mhweb.spigot.GlassSpigot
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.function.Consumer

object PlayerListeners: Listener {

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        Glass.updateCount(TrackedCount.ONLINE_PLAYERS)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskLater(GlassSpigot.getInstance(), Consumer {
            Glass.updateCount(TrackedCount.ONLINE_PLAYERS)
        }, 1)
    }

}