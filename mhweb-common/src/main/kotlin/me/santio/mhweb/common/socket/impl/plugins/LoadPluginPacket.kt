package me.santio.mhweb.common.socket.impl.plugins

import io.socket.client.Ack
import me.santio.mhweb.common.GlassPluginManager
import me.santio.mhweb.common.socket.SocketEvent

class LoadPluginPacket: SocketEvent("LOAD_PLUGIN") {

    override fun onEvent(vararg data: Any) {

        val name = data[0] as String
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