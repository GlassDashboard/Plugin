package me.santio.mhweb.common.models.logs.impl

import kotlinx.serialization.Serializable
import me.santio.mhweb.common.models.logs.Loggable
import me.santio.mhweb.common.socketOld.packets.BasePacket

@Serializable
data class ConsoleLog(
    val log: String,
    val level: String,
): Loggable
