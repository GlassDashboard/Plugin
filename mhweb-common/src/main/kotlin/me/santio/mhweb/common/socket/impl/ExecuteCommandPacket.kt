package me.santio.mhweb.common.socket.impl

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.logs.impl.CommandLog
import me.santio.mhweb.common.models.packets.Command
import me.santio.mhweb.common.socket.SocketEvent

class ExecuteCommandPacket: SocketEvent("EXECUTE_COMMAND") {

    override fun onEvent(vararg data: Any) {

        val command = Glass.json.decodeFromString<Command>(data[0] as String)
        Glass.sendLog(System.currentTimeMillis().toString(), CommandLog(command.user, command.original))

        Glass.server.executeCommand(command.command)

    }

}