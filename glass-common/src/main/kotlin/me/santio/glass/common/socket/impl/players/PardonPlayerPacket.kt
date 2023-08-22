package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent
import java.util.*

class PardonPlayerPacket: me.santio.glass.common.socket.SocketEvent("PARDON_PLAYER") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0] as String)
        val acknowledgement = data[1] as Ack

        val success = me.santio.glass.common.Glass.server.pardonPlayer(uuid)
        acknowledgement.call(success)

    }

}