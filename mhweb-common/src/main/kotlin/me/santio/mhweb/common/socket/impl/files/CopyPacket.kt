package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketEvent

class CopyPacket: SocketEvent("COPY_FILE") {

    override fun onEvent(vararg data: Any) {

        val from = Glass.json.decodeFromString<FileLocation>(data[0] as String)
        val to = Glass.json.decodeFromString<FileLocation>(data[1] as String)
        val acknowledgement = data[2] as Ack

        GlassFileManager.copyFile(from, to)
        acknowledgement.call()

    }

}