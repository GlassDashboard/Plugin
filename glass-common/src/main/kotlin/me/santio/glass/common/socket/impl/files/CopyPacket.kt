package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class CopyPacket : SocketEvent("COPY_FILE") {

    override fun onEvent(vararg data: Any) {

        val from =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0].toString()
            )
        val to =
            Glass.json.decodeFromString<ResolvablePath>(
                data[1].toString()
            )
        val acknowledgement = data[2] as Ack

        GlassFileManager.copyFile(from, to)
        acknowledgement.call()

    }

}