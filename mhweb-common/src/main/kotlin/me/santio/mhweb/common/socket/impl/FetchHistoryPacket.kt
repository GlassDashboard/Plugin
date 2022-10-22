package me.santio.mhweb.common.socket.impl

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.packets.ConsoleHistory
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketEvent

class FetchHistoryPacket: SocketEvent("FETCH_CONSOLE_HISTORY") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        acknowledgement.call(Glass.json.encodeToString(ConsoleHistory(Glass.logs)))

    }

}