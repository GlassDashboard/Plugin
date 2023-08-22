//package me.santio.glass.common.socket.impl.files
//
//import io.socket.client.Ack
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.decodeFromString
//import me.santio.glass.common.Glass
//import me.santio.glass.common.GlassFileManager
//import me.santio.glass.common.models.packets.ResolvablePath
//import me.santio.glass.common.socket.SocketEvent
//
//class FetchAllFilesPacket: SocketEvent("ALL_FILES") {
//
//    override fun onEvent(vararg data: Any) {
//
//        val location: ResolvablePath = Glass.json.decodeFromString(data[0] as String)
//        val acknowledgement = data[1] as Ack
//
//        val files = GlassFileManager.fetchAllFiles(location.path, location.root)
//        acknowledgement.call(Glass.json.encodeToString(files))
//
//    }
//
//}