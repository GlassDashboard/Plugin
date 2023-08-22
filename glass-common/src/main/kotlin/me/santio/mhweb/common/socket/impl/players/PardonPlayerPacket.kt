package me.santio.mhweb.common.socket.impl.players

import io.socket.client.Ack
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.socket.SocketEvent
import java.util.*

class PardonPlayerPacket: SocketEvent("PARDON_PLAYER") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0] as String)
        val acknowledgement = data[1] as Ack

        val success = Glass.server.pardonPlayer(uuid)
        acknowledgement.call(success)

    }

}