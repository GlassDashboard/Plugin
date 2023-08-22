package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.ResolvablePath
import me.santio.mhweb.common.socket.SocketEvent

class CreateFilePacket: SocketEvent("file:create") {

    override fun onEvent(vararg data: Any) {

        val location: ResolvablePath = Glass.json.decodeFromString(data[0] as String)
        val type = data[1] as String
        val acknowledgement = data[2] as Ack

        if (type.lowercase() == "directory")
            GlassFileManager.createDirectory(location)
        else GlassFileManager.createFile(location)

        acknowledgement.call()
    }

}