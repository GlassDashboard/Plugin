package me.santio.mhweb.spigot.tasks

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.TrackedCount
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

object WhitelistCheckTask: Consumer<BukkitTask> {
    private var whitelistCount = Bukkit.getWhitelistedPlayers().size

    override fun accept(t: BukkitTask) {
        if (Bukkit.getWhitelistedPlayers().size != whitelistCount) {
            whitelistCount = Bukkit.getWhitelistedPlayers().size
            Glass.updateCount(TrackedCount.WHITELISTED_PLAYERS)
        }
    }
}