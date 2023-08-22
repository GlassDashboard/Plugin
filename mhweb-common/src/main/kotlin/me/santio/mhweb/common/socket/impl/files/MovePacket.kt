package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.ResolvablePath
import me.santio.mhweb.common.socket.SocketEvent

class MovePacket: SocketEvent("MOVE_FILE") {

    override fun onEvent(vararg data: Any) {

        val from = Glass.json.decodeFromString<ResolvablePath>(data[0] as String)
        val to = Glass.json.decodeFromString<ResolvablePath>(data[1] as String)
        val acknowledgement = data[2] as Ack

        GlassFileManager.moveFile(from, to)
        acknowledgement.call()

    }

}