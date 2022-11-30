package me.santio.mhweb.spigot.tasks

import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.TrackedCount
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

object OpCheckTask: Consumer<BukkitTask> {
    private var operatorCount = Bukkit.getOperators().size

    override fun accept(t: BukkitTask) {
        if (Bukkit.getOperators().size != operatorCount) {
            operatorCount = Bukkit.getOperators().size
            Glass.updateCount(TrackedCount.ADMINISTRATOR_PLAYERS)
        }
    }
}