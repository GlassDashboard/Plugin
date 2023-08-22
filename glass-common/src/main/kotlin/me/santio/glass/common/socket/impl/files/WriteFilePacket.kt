package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString

class WriteFilePacket : SocketEvent("file:write") {

    override fun onEvent(vararg data: Any) {

        val location =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0] as String
            )
        val content = data[1] as String
        val acknowledgement = data[2] as Ack

        GlassFileManager.writeFile(location, content)
        acknowledgement.call()

    }

}