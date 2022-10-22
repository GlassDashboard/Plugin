package me.santio.mhweb.common.adapter

import me.santio.mhweb.common.models.packets.TinyPlayer

abstract class ServerAdapter {
    abstract fun executeCommand(command: String)
    abstract fun async(code: () -> Unit)
    abstract fun getOnlinePlayers(): List<TinyPlayer>
}