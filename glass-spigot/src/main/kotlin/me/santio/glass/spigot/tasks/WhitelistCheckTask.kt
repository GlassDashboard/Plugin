package me.santio.glass.spigot.tasks

import me.santio.glass.common.Glass
import me.santio.glass.common.models.TrackedCount
import org.bukkit.Bukkit

object WhitelistCheckTask: Runnable {
    private var whitelistCount = Bukkit.getWhitelistedPlayers().size

    override fun run() {
        if (Bukkit.getWhitelistedPlayers().size != whitelistCount) {
            whitelistCount = Bukkit.getWhitelistedPlayers().size
            Glass.updateCount(TrackedCount.WHITELISTED_PLAYERS)
        }
    }
}