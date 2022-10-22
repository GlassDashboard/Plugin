package me.santio.mhweb.common.socketOld.packets.client

import me.santio.mhweb.common.models.packets.FileData
import me.santio.mhweb.common.socketOld.packets.BasePacket

data class AllFilesDataPacket(
    val files: List<FileData>
): BasePacket("ALL_FILES")
