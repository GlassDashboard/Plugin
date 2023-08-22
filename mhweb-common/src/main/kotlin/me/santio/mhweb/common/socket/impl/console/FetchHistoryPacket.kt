package me.santio.mhweb.common.socket.impl.console

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.packets.ConsoleHistory
import me.santio.mhweb.common.socket.SocketEvent

class FetchHistoryPacket: SocketEvent("console:history") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val logs = Glass.json.encodeToString(ConsoleHistory(Glass.logs))

        acknowledgement.call(logs)

    }

}