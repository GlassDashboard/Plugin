//package me.santio.mhweb.common.socket.impl.files
//
//import io.socket.client.Ack
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.decodeFromString
//import me.santio.mhweb.common.Glass
//import me.santio.mhweb.common.GlassFileManager
//import me.santio.mhweb.common.models.packets.ResolvablePath
//import me.santio.mhweb.common.socket.SocketEvent
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