package me.santio.glass.common.socket.impl.players

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.glass.common.Glass
import me.santio.glass.common.socket.SocketEvent

class FetchBlacklistPacket: me.santio.glass.common.socket.SocketEvent("FETCH_BLACKLIST") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val players = me.santio.glass.common.Glass.server.getBannedPlayers()

        acknowledgement.call(me.santio.glass.common.Glass.json.encodeToString(players))

    }

}