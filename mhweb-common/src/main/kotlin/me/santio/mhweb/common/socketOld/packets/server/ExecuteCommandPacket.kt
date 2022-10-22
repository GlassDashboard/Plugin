package me.santio.mhweb.common.socketOld.packets.server

import me.santio.mhweb.common.models.logs.Loggable
import me.santio.mhweb.common.socketOld.packets.BasePacket

data class ExecuteCommandPacket(
    val command: String,
    val user: String,
    val raw: String
): BasePacket("EXECUTE_COMMAND"), Loggable
