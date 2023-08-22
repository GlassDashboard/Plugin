package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.FileChanges
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent

class ChangeFilePacket: me.santio.glass.common.socket.SocketEvent("file:change") {

    override fun onEvent(vararg data: Any) {

        var location = me.santio.glass.common.Glass.json.decodeFromString<me.santio.glass.common.models.packets.ResolvablePath>(data[0] as String)

        val changes = me.santio.glass.common.Glass.json.decodeFromString<me.santio.glass.common.models.packets.FileChanges>(data[1] as String)
        val newPath = changes.path?.let { me.santio.glass.common.models.packets.ResolvablePath(it) }

        val acknowledgement = data[2] as Ack

        if (newPath != null && newPath != location) {
            if (changes.copy == true) me.santio.glass.common.GlassFileManager.copyFile(location, newPath)
            else me.santio.glass.common.GlassFileManager.moveFile(location, newPath)

            location = newPath
        }

        if (changes.content != null) me.santio.glass.common.GlassFileManager.writeFile(location, changes.content)
        if (changes.unarchive == true) me.santio.glass.common.GlassFileManager.unarchive(location)

        acknowledgement.call()

    }

}