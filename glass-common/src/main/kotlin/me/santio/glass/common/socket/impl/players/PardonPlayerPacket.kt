package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent
import java.util.*

class PardonPlayerPacket : SocketEvent("PARDON_PLAYER") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0].toString())
        val acknowledgement = data[1] as Ack

        val success = Glass.server.pardonPlayer(uuid)
        acknowledgement.call(success)

    }

}