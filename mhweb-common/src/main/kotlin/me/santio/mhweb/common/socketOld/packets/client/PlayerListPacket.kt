package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class PlayerListPacket(
    val players: List<MiniPlayer>
): BasePacket("PLAYER_LIST")

data class MiniPlayer(
    val name: String,
    val uuid: String,
    val opped: Boolean
)
