package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketEvent

class CreateFilePacket: SocketEvent("CREATE_FILE") {

    override fun onEvent(vararg data: Any) {

        val location: FileLocation = Glass.json.decodeFromString(data[0] as String)
        val acknowledgement = data[1] as Ack

        GlassFileManager.createFile(location)
        acknowledgement.call()

    }

}