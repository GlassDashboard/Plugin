package me.santio.glass.spigot

import me.santio.glass.common.adapter.ServerPlugin
import org.bukkit.plugin.Plugin
import java.io.File

fun Plugin.toGlassPlugin(): ServerPlugin {
    val method = this.javaClass.getDeclaredMethod("getFile");
    method.isAccessible = true
    val file = method.invoke(this) as File

    return ServerPlugin(
        file,
        this.name,
        this.description.version,
        this.description.description ?: "No description provided",
        this.description.authors,
        this.isEnabled
    )
}