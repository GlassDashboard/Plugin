package me.santio.mhweb.common.socket.impl.players

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.socket.SocketEvent

class FetchWhitelistedPacket: SocketEvent("FETCH_WHITELISTED_PLAYERS") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val players = Glass.server.getWhitelistedPlayers()

        acknowledgement.call(Glass.json.encodeToString(players))

    }

}