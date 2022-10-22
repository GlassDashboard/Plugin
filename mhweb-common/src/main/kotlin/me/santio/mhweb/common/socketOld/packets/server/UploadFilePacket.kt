package me.santio.mhweb.common.socketOld.packets.server

import me.santio.mhweb.common.socketOld.packets.BasePacket

data class UploadFilePacket(
    val path: String,
    val user: String,
    val replace: Boolean,
    val root: Boolean
): BasePacket("UPLOAD_FILE")