package me.santio.mhweb.common.socket.impl

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketEvent

class FetchPlayersPacket: SocketEvent("FETCH_PLAYERS") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val players = Glass.server.getOnlinePlayers()

        acknowledgement.call(Glass.json.encodeToString(players))

    }

}