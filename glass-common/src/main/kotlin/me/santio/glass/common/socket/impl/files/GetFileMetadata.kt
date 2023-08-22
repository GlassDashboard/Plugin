package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent
import me.santio.glass.common.socket.SocketHandler.toPath
import org.json.JSONObject

class GetFileMetadata: me.santio.glass.common.socket.SocketEvent("file:metadata") {

    override fun onEvent(vararg data: Any) {
        println("Got file metadata request")
        val location: me.santio.glass.common.models.packets.ResolvablePath = (data[0] as JSONObject).toPath()
        val acknowledgement = data[1] as Ack

        println("Got file metadata request for $location")

        val file = me.santio.glass.common.GlassFileManager.getFileMetadata(location)

        println("Sending file metadata: " + me.santio.glass.common.Glass.json.encodeToString(file))
        acknowledgement.call(me.santio.glass.common.Glass.json.encodeToString(file))
    }

}