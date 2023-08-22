package me.santio.mhweb.common.socket.impl.players

import io.socket.client.Ack
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.socket.SocketEvent
import java.util.*

class PlayerAdministratorPacket: SocketEvent("SET_ADMINISTRATOR") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0] as String)
        val state: Boolean = data[1] as Boolean
        val acknowledgement = data[2] as Ack

        val success = Glass.server.setAdministrator(uuid, state)
        acknowledgement.call(success)

    }

}