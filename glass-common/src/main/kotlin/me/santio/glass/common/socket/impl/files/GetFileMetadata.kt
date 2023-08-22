package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent
import me.santio.glass.common.socket.SocketHandler.toPath
import org.json.JSONObject

class GetFileMetadata : SocketEvent("file:metadata") {

    override fun onEvent(vararg data: Any) {
        println("Got file metadata request")
        val location: ResolvablePath = (data[0] as JSONObject).toPath()
        val acknowledgement = data[1] as Ack

        println("Got file metadata request for $location")

        val file = GlassFileManager.getFileMetadata(location)

        println("Sending file metadata: " + Glass.json.encodeToString(file))
        acknowledgement.call(Glass.json.encodeToString(file))
    }

}