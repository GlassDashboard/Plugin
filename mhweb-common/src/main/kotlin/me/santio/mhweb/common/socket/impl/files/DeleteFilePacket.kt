package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.ResolvablePath
import me.santio.mhweb.common.socket.SocketEvent

class DeleteFilePacket: SocketEvent("file:delete") {

    override fun onEvent(vararg data: Any) {

        val location = Glass.json.decodeFromString<ResolvablePath>(data[0] as String)
        val acknowledgement = data[1] as Ack

        GlassFileManager.deleteFile(location)
        acknowledgement.call()

    }

}