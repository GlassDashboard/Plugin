package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.models.packets.FileData
import me.santio.mhweb.common.socketOld.packets.BasePacket

data class FileDataPacket(
    val file: FileData
): BasePacket("FILE_DATA")
