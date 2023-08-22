package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class WriteFilePacket: me.santio.glass.common.socket.SocketEvent("file:write") {

    override fun onEvent(vararg data: Any) {

        val location = me.santio.glass.common.Glass.json.decodeFromString<me.santio.glass.common.models.packets.ResolvablePath>(data[0] as String)
        val content = data[1] as String
        val acknowledgement = data[2] as Ack

        me.santio.glass.common.GlassFileManager.writeFile(location, content)
        acknowledgement.call()

    }

}