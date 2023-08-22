package me.santio.glass.spigot.tasks

import me.santio.glass.common.Glass
import me.santio.glass.common.models.TrackedCount
import org.bukkit.Bukkit

object OpCheckTask: Runnable {
    private var operatorCount = Bukkit.getOperators().size

    override fun run() {
        if (Bukkit.getOperators().size != operatorCount) {
            operatorCount = Bukkit.getOperators().size
            Glass.updateCount(TrackedCount.ADMINISTRATOR_PLAYERS)
        }
    }
}