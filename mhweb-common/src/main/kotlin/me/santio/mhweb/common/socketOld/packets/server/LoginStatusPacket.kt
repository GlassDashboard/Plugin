package me.santio.mhweb.common.socketOld.packets.server

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class LoginStatusPacket(
    val ok: Boolean,
    val message: String?,
    val server: String?
): BasePacket("LOGIN_STATUS")
