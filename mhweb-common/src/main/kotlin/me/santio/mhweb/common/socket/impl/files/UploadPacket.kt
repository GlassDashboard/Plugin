package me.santio.mhweb.common.socket.impl.files

import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.ResolvablePath
import me.santio.mhweb.common.socket.SocketEvent

class UploadPacket: SocketEvent("UPLOAD_FILE") {

    override fun onEvent(vararg data: Any) {

        val location = Glass.json.decodeFromString<ResolvablePath>(data[0] as String)
        val id = data[1] as String

        GlassFileManager.uploadFile(location, id)

    }

}