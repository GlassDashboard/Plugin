package me.santio.glass.common.socket.impl.files

import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class UploadPacket : SocketEvent("UPLOAD_FILE") {

    override fun onEvent(vararg data: Any) {

        val location =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0] as String
            )
        val id = data[1] as String

        GlassFileManager.uploadFile(location, id)

    }

}