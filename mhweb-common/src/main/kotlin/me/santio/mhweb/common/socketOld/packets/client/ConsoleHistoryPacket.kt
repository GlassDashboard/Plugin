package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.models.logs.Logged
import me.santio.mhweb.common.socketOld.packets.BasePacket

data class ConsoleHistoryPacket(
    val history: List<Logged>
): BasePacket("CONSOLE_HISTORY")
