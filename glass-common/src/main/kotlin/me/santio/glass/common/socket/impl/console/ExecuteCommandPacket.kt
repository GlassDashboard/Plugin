package me.santio.glass.common.socket.impl.console

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.models.logs.impl.CommandLog
import me.santio.glass.common.models.packets.Command
import me.santio.glass.common.socket.SocketEvent

class ExecuteCommandPacket : SocketEvent("console:execute") {

    override fun onEvent(vararg data: Any) {

        val command = Glass.json.decodeFromString<Command>(data[0].toString())
        val acknowledgement = data[1] as Ack

        Glass.sendLog(
            System.currentTimeMillis().toString(),
            CommandLog(command.user, command.original), true
        )

        Glass.server.executeCommand(command.command)
        acknowledgement.call()

    }

}