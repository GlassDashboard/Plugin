package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class DeleteFilePacket : SocketEvent("file:delete") {

    override fun onEvent(vararg data: Any) {

        val location =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0] as String
            )
        val acknowledgement = data[1] as Ack

        GlassFileManager.deleteFile(location)
        acknowledgement.call()

    }

}