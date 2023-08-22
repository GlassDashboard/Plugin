package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent
import java.util.*

class KickPlayerPacket : SocketEvent("KICK_PLAYER") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0] as String)

        var reason: String? = data[1] as String?
        if (reason == null) reason = "No reason provided"

        val acknowledgement = data[2] as Ack

        val success = Glass.server.kickPlayer(uuid, reason)
        acknowledgement.call(success)

    }

}