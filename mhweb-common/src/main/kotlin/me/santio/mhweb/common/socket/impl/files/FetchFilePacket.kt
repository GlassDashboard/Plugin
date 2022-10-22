package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketEvent

class FetchFilePacket: SocketEvent("FETCH_FILE") {

    override fun onEvent(vararg data: Any) {

        val location: FileLocation = Glass.json.decodeFromString(data[0] as String)
        val acknowledgement = data[1] as Ack

        val file = GlassFileManager.fetchFile(location.path, location.root)

        acknowledgement.call(Glass.json.encodeToString(file))

    }

}