package me.santio.mhweb.common.socket.impl.console

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.logs.impl.CommandLog
import me.santio.mhweb.common.models.packets.Command
import me.santio.mhweb.common.socket.SocketEvent

class ExecuteCommandPacket: SocketEvent("console:execute") {

    override fun onEvent(vararg data: Any) {

        val command = Glass.json.decodeFromString<Command>(data[0] as String)
        val acknowledgement = data[1] as Ack

        Glass.sendLog(System.currentTimeMillis().toString(), CommandLog(command.user, command.original), true)

        Glass.server.executeCommand(command.command)
        acknowledgement.call()

    }

}