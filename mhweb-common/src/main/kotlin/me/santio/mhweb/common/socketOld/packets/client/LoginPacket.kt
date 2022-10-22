package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class LoginPacket(
    val token: String
): BasePacket("LOGIN")
