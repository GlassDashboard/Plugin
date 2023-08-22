package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent
import java.util.*

class WhitelistPlayerPacket: me.santio.glass.common.socket.SocketEvent("SET_WHITELIST") {

    override fun onEvent(vararg data: Any) {

        val uuid = UUID.fromString(data[0] as String)
        val state: Boolean = data[1] as Boolean
        val acknowledgement = data[2] as Ack

        val success = me.santio.glass.common.Glass.server.setWhitelisted(uuid, state)
        acknowledgement.call(success)

    }

}