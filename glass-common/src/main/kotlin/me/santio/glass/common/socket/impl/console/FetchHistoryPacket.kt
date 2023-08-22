package me.santio.glass.common.socket.impl.console

import io.socket.client.Ack
import kotlinx.serialization.encodeToString
import me.santio.glass.common.Glass
import me.santio.glass.common.models.packets.ConsoleHistory
import me.santio.glass.common.socket.SocketEvent

class FetchHistoryPacket: me.santio.glass.common.socket.SocketEvent("console:history") {

    override fun onEvent(vararg data: Any) {

        val acknowledgement = data[0] as Ack
        val logs = me.santio.glass.common.Glass.json.encodeToString(
            me.santio.glass.common.models.packets.ConsoleHistory(
                me.santio.glass.common.Glass.logs
            )
        )

        acknowledgement.call(logs)

    }

}