package me.santio.glass.spigot.listeners

import me.santio.glass.common.Glass
import me.santio.glass.common.models.TrackedCount
import me.santio.glass.spigot.GlassSpigot
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerListeners: Listener {

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        Glass.updateCount(TrackedCount.ALL)
    }

    @EventHandler
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskLater(GlassSpigot.getInstance(), {
            Glass.updateCount(TrackedCount.ALL)
        }, 1)
    }

}