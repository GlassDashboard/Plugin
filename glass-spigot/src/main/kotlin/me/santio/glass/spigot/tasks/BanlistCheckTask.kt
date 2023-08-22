package me.santio.glass.spigot.tasks

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.TrackedCount
import org.bukkit.Bukkit

object BanlistCheckTask: Runnable {
    private var bannedCount = Bukkit.getBannedPlayers().size

    override fun run() {
        if (Bukkit.getBannedPlayers().size != bannedCount) {
            bannedCount = Bukkit.getBannedPlayers().size
            Glass.updateCount(TrackedCount.BLACKLISTED_PLAYERS)
        }
    }
}