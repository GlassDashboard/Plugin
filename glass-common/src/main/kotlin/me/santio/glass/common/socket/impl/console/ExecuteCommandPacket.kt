package me.santio.glass.common.socket.impl.console

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.santio.glass.common.Glass
import me.santio.glass.common.models.logs.impl.CommandLog
import me.santio.glass.common.models.packets.Command
import me.santio.glass.common.socket.SocketEvent

class ExecuteCommandPacket: me.santio.glass.common.socket.SocketEvent("console:execute") {

    override fun onEvent(vararg data: Any) {

        val command = me.santio.glass.common.Glass.json.decodeFromString<me.santio.glass.common.models.packets.Command>(data[0] as String)
        val acknowledgement = data[1] as Ack

        me.santio.glass.common.Glass.sendLog(System.currentTimeMillis().toString(),
            me.santio.glass.common.models.logs.impl.CommandLog(command.user, command.original), true)

        me.santio.glass.common.Glass.server.executeCommand(command.command)
        acknowledgement.call()

    }

}