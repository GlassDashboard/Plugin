package me.santio.mhweb.spigot.tasks

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.TrackedCount
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

object BanlistCheckTask: Consumer<BukkitTask> {
    private var bannedCount = Bukkit.getBannedPlayers().size

    override fun accept(t: BukkitTask) {
        if (Bukkit.getBannedPlayers().size != bannedCount) {
            bannedCount = Bukkit.getBannedPlayers().size
            Glass.updateCount(TrackedCount.BLACKLISTED_PLAYERS)
        }
    }
}