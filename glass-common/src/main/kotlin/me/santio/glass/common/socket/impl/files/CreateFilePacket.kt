package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class CreateFilePacket : SocketEvent("file:create") {

    override fun onEvent(vararg data: Any) {

        val location: ResolvablePath =
            Glass.json.decodeFromString(data[0].toString())
        val type = data[1].toString()
        val acknowledgement = data[2] as Ack

        if (type.lowercase() == "directory")
            GlassFileManager.createDirectory(location)
        else GlassFileManager.createFile(location)

        acknowledgement.call()
    }

}