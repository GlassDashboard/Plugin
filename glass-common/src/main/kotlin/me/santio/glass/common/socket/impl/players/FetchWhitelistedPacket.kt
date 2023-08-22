package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent

class FetchWhitelistedPacket: me.santio.glass.common.socket.SocketEvent("FETCH_WHITELISTED_PLAYERS") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val players = me.santio.glass.common.Glass.server.getWhitelistedPlayers()

        acknowledgement.call(me.santio.glass.common.Glass.json.encodeToString(players))

    }

}