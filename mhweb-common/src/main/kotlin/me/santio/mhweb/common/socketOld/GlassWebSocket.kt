package me.santio.mhweb.common.socketOld
//
//import me.santio.mhweb.common.Glass
//import me.santio.mhweb.common.models.packets.FetchFilePacket
//import me.santio.mhweb.common.socketOld.packets.BasePacket
//import me.santio.mhweb.common.socketOld.packets.client.*
//import me.santio.mhweb.common.socketOld.packets.server.*
//import java.io.File
//import java.net.http.WebSocket
//import java.nio.ByteBuffer
//import java.util.*
//import kotlin.concurrent.schedule
//
//class GlassWebSocket(uri: String): BaseWebSocket.Json<BasePacket>(
//    uri,
//    BasePacket::class.java,
//    true
//) {
//
//    companion object {
//        var authenticated = false
//    }
//
//    override fun onConnect() {
//        Glass.log("Successfully connected to Glass Socket")
//        authenticated = false
//
//        // Send the login packet
//        send(LoginPacket(Glass.token!!))
//    }
//
//    override fun onJson(json: BasePacket, raw: String) {
//        when(json.type.trim().uppercase()) {
//            // Pinging
//            "PING" -> send(BasePacket("PONG"))
//
//            // Login Flow
//            "LOGIN_STATUS" -> {
//                val status = gson.fromJson(raw, LoginStatusPacket::class.java)
//
//                if (!status.ok) {
//                    Glass.log("Failed to authenticate with Glass")
//                    Glass.log("Reason: ${status.message!!}")
//                    Glass.log("Attempting reconnect in 10 seconds...")
//                    Timer().schedule(10000) {
//                        reconnect()
//                    }
//                    return
//                } else if (!status.server!!.equals(Glass.SERVER_ID, ignoreCase = true)) {
//                    Glass.log("Failed to match server ids. Make sure you added the plugin to the correct server, and that the token is correct.")
//                    close()
//                    return
//                }
//
//                Glass.log("Successfully authenticated with Glass")
//                authenticated = true
//            }
//
//            // Console
//            "CONSOLE_HISTORY" -> {
//                send(ConsoleHistoryPacket(Glass.logs))
//            }
//            "EXECUTE_COMMAND" -> {
//                val data = gson.fromJson(raw, ExecuteCommandPacket::class.java)
//                Glass.server.executeCommand(data.command)
//                Glass.sendLog(System.currentTimeMillis().toString(), data, false)
//            }
//
//            // Player Management
//            "GET_PLAYERS" -> {
//                send(PlayerListPacket(
//                    Glass.server.getOnlinePlayers()
//                ))
//            }
//
//            // File Management
//            "FETCH_FILE" -> {
//                val data = gson.fromJson(raw, FetchFilePacket::class.java)
//                Glass.log("Fetching file, root: ${data.root}, path: ${data.path}")
//                send(FileDataPacket(Glass.fetchFile(data.path, root = data.root)))
//            }
//            "FETCH_ALL_FILES" -> {
//                Glass.server.async {
//                    val data = gson.fromJson(raw, FetchFilePacket::class.java)
//                    Glass.log("Fetching all files, root: ${data.root}, path: ${data.path}")
//                    send(AllFilesDataPacket(Glass.fetchAllFiles(data.path, root = data.root)))
//                }
//            }
//            "DOWNLOAD_FILE" -> {
//                val data = gson.fromJson(raw, DownloadFilePacket::class.java)
//                Glass.log("Downloading file, root: ${data.root}, path: ${data.path}")
//                val file = Glass.fileFromPath(data.path, root = data.root)
//
//                if (!file.exists()) return
//
//                Glass.server.async {
//                    Glass.downloadFile(file, data.user)
//                }
//            }
//            "UPLOAD_FILE" -> {
//                Glass.log("Upload request received, data: $raw")
//                val data = gson.fromJson(raw, UploadFilePacket::class.java)
//                var file = Glass.fileFromPath(data.path, root = data.root)
//
//                if (file.exists()) {
//                    if (data.replace) file.delete()
//                    else {
//                        val name = file.nameWithoutExtension
//                        var i = 1
//                        while (file.exists()) {
//                            file = File(file.parentFile, "$name ($i).${file.extension}")
//                            i++
//                        }
//                    }
//                }
//
//                file.createNewFile()
//                downloading = data
//            }
//            "EOF" -> {
//                downloading = null
//            }
//         }
//    }
//
//    private var downloading: UploadFilePacket? = null
//    override fun onBinary(bytes: ByteBuffer) {
//        Glass.log("Received ${bytes.remaining()} bytes")
//        if (downloading == null) return
//        Glass.log("Download verified, writing to file")
//        val file = Glass.fileFromPath(downloading!!.path, root = downloading!!.root)
//        // Write more bytes to file
//        file.appendBytes(bytes.array())
//        Glass.log("Wrote ${bytes.remaining()} bytes to file, total of ${file.length()} bytes")
//    }
//
//    override fun onClose(code: Int, reason: String) {
//        Glass.log("Disconnected from Glass Socket (Code $code@$reason)")
//        authenticated = false
//    }
//
//    override fun onJsonParseError(exception: Exception, message: String) {
//        exception.printStackTrace()
//        Glass.log("Error parsing JSON: $message")
//        Glass.log("Please report this to a developer!")
//    }
//
//    override fun onError(webSocket: WebSocket, exception: Throwable) {
//        exception.printStackTrace()
//    }
//}