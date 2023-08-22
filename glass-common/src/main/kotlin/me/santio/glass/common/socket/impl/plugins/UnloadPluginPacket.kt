package me.santio.glass.common.socket.impl.plugins

import io.socket.client.Ack
import me.santio.glass.common.GlassPluginManager
import me.santio.glass.common.socket.SocketEvent

class UnloadPluginPacket: me.santio.glass.common.socket.SocketEvent("LOAD_PLUGIN") {

    override fun onEvent(vararg data: Any) {

        val name = data[0] as String
        val ack = data[1] as Ack

        val plugin = me.santio.glass.common.GlassPluginManager.getPlugin(name) ?: run {
            ack.call(false)
            return
        }

        me.santio.glass.common.GlassPluginManager.unloadPlugin(plugin)
        ack.call(true)

    }

}