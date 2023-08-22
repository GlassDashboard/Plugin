package me.santio.mhweb.common.socket.impl.plugins

import io.socket.client.Ack
import kong.unirest.Unirest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.santio.mhweb.common.GlassPluginManager
import me.santio.mhweb.common.socket.SocketEvent
import java.io.File

class DownloadPluginPacket: SocketEvent("DOWNLOAD_PLUGIN") {

    override fun onEvent(vararg data: Any): Unit = runBlocking {

        val name = data[0] as String
        val url = data[1] as String
        val ack = data[2] as Ack

        launch {
            Unirest.get(url)
                .asFile(File(GlassPluginManager.pluginFolder, "$name.jar").absolutePath)
                .body

            ack.call(true)
        }

    }

}