package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class WriteFilePacket : SocketEvent("file:write") {

    override fun onEvent(vararg data: Any) {

        val location =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0].toString()
            )
        val content = data[1].toString()
        val acknowledgement = data[2] as Ack

        GlassFileManager.writeFile(location, content)
        acknowledgement.call()

    }

}