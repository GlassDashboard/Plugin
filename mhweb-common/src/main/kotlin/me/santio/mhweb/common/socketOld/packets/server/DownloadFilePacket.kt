package me.santio.mhweb.common.socketOld.packets.server

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class DownloadFilePacket(
    val path: String,
    val user: String,
    val root: Boolean
): BasePacket("DOWNLOAD_FILE")