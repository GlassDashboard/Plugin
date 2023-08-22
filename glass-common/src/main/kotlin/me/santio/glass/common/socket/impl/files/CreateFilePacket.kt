package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class CreateFilePacket: me.santio.glass.common.socket.SocketEvent("file:create") {

    override fun onEvent(vararg data: Any) {

        val location: me.santio.glass.common.models.packets.ResolvablePath = me.santio.glass.common.Glass.json.decodeFromString(data[0] as String)
        val type = data[1] as String
        val acknowledgement = data[2] as Ack

        if (type.lowercase() == "directory")
            me.santio.glass.common.GlassFileManager.createDirectory(location)
        else me.santio.glass.common.GlassFileManager.createFile(location)

        acknowledgement.call()
    }

}