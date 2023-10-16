package me.santio.glass.common.socket.impl.plugins

import io.socket.client.Ack
import me.santio.glass.common.GlassPluginManager
import me.santio.glass.common.socket.SocketEvent

class LoadPluginPacket : SocketEvent("LOAD_PLUGIN") {

    override fun onEvent(vararg data: Any) {

        val name = data[0].toString()
        val ack = data[1] as Ack

        val plugin = GlassPluginManager.getPluginFile(name)
        if (!plugin.exists()) {
            ack.call(false)
            return
        }

        GlassPluginManager.loadPlugin(plugin)
        ack.call(true)

    }

}