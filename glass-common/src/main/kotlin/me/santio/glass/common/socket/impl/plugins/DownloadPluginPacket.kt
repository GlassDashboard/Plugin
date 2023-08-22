package me.santio.glass.common.socket.impl.plugins

import io.socket.client.Ack
import kong.unirest.Unirest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.santio.glass.common.GlassPluginManager
import me.santio.glass.common.socket.SocketEvent
import java.io.File

class DownloadPluginPacket: me.santio.glass.common.socket.SocketEvent("DOWNLOAD_PLUGIN") {

    override fun onEvent(vararg data: Any): Unit = runBlocking {

        val name = data[0] as String
        val url = data[1] as String
        val ack = data[2] as Ack

        launch {
            Unirest.get(url)
                .asFile(File(me.santio.glass.common.GlassPluginManager.pluginFolder, "$name.jar").absolutePath)
                .body

            ack.call(true)
        }

    }

}