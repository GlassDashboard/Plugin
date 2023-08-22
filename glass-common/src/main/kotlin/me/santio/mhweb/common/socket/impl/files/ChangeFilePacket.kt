package me.santio.mhweb.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.GlassFileManager
import me.santio.mhweb.common.models.packets.FileChanges
import me.santio.mhweb.common.models.packets.ResolvablePath
import me.santio.mhweb.common.socket.SocketEvent

class ChangeFilePacket: SocketEvent("file:change") {

    override fun onEvent(vararg data: Any) {

        var location = Glass.json.decodeFromString<ResolvablePath>(data[0] as String)

        val changes = Glass.json.decodeFromString<FileChanges>(data[1] as String)
        val newPath = changes.path?.let { ResolvablePath(it) }

        val acknowledgement = data[2] as Ack

        if (newPath != null && newPath != location) {
            if (changes.copy == true) GlassFileManager.copyFile(location, newPath)
            else GlassFileManager.moveFile(location, newPath)

            location = newPath
        }

        if (changes.content != null) GlassFileManager.writeFile(location, changes.content)
        if (changes.unarchive == true) GlassFileManager.unarchive(location)

        acknowledgement.call()

    }

}