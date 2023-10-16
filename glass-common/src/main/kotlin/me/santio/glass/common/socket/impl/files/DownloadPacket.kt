package me.santio.glass.common.socket.impl.files

import io.socket.client.Ack
import kotlinx.serialization.decodeFromString
import me.santio.glass.common.Glass
import me.santio.glass.common.GlassFileManager
import me.santio.glass.common.models.packets.CDNMetadata
import me.santio.glass.common.models.packets.ResolvablePath
import me.santio.glass.common.socket.SocketEvent
import java.net.URI
import java.util.concurrent.Executors

class DownloadPacket : SocketEvent("file:download") {

    override fun onEvent(vararg data: Any) {

        val location =
            Glass.json.decodeFromString<ResolvablePath>(
                data[0].toString()
            )

        println("Location: $location")

        val files = Glass.json.decodeFromString<List<CDNMetadata>>(data[1].toString())
        val acknowledgement = data[2] as Ack

        Glass.log("Pre for loop")

        val downloading: MutableList<CDNMetadata> = files.toMutableList()

        for (file in files) {
            println(file)
            Executors.newSingleThreadExecutor().submit {
                val path = location.getFile().toPath().resolve(file.name).toFile()
                val download = URI(Glass.api_uri + file.url)
                Glass.log(download.toString())
                GlassFileManager.downloadFile(path, download) {
                    if (!it) Glass.log("Failed to download ${file.name} from Glass")

                    downloading.remove(file)
                    if (downloading.isEmpty()) acknowledgement.call()
                }
            }
        }
    }

}