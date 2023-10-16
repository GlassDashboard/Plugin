//package me.santio.glass.common.socket.impl.files
//
//import io.socket.client.Ack
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import me.santio.glass.common.Glass
//import GlassFileManager
//import ResolvablePath
//import me.santio.glass.common.socket.SocketEvent
//
//class FetchFilePacket: SocketEvent("FETCH_FILE") {
//
//    override fun onEvent(vararg data: Any) {
//
//        val location: ResolvablePath = Glass.json.decodeFromString(data[0].toString())
//        val acknowledgement = data[1] as Ack
//
//        val file = GlassFileManager.fetchFile(location.path, root = location.root)
//        acknowledgement.call(Glass.json.encodeToString(file))
//
//    }
//
//}