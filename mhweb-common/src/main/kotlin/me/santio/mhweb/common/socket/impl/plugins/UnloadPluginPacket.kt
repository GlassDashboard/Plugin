package me.santio.mhweb.common.socket.impl.plugins

import io.socket.client.Ack
import me.santio.mhweb.common.GlassPluginManager
import me.santio.mhweb.common.socket.SocketEvent

class UnloadPluginPacket: SocketEvent("LOAD_PLUGIN") {

    override fun onEvent(vararg data: Any) {

        val name = data[0] as String
        val ack = data[1] as Ack

        val plugin = GlassPluginManager.getPlugin(name) ?: run {
            ack.call(false)
            return
        }

        GlassPluginManager.unloadPlugin(plugin)
        ack.call(true)

    }

}