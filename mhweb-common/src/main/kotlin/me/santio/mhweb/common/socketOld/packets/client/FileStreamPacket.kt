package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class FileStreamPacket(
    val name: String,
    val size: Long,
    val user: String
): BasePacket("FILE_STREAM")