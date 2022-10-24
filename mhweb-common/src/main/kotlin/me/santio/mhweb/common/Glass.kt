package me.santio.mhweb.common

import io.socket.client.Ack
import io.socket.client.Socket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.santio.mhweb.common.adapter.ServerAdapter
import me.santio.mhweb.common.models.packets.FileData
import me.santio.mhweb.common.models.logs.Loggable
import me.santio.mhweb.common.models.logs.Logged
import me.santio.mhweb.common.models.logs.impl.CommandLog
import me.santio.mhweb.common.models.logs.impl.ConsoleLog
import me.santio.mhweb.common.models.packets.FileLocation
import me.santio.mhweb.common.socket.SocketHandler
import me.santio.mhweb.common.socketOld.packets.client.FileStreamPacket
import me.santio.mhweb.common.socketOld.packets.client.PlayerListPacket
import me.santio.mhweb.common.utils.Zipper
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

object Glass {
    lateinit var server: ServerAdapter

    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Loggable::class) {
                subclass(CommandLog::class)
                subclass(ConsoleLog::class)
            }
        }
    }

    var token: String? = null
    var serverType: ServerType = ServerType.SPIGOT
    var socket: Socket? = null

    val logs: MutableList<Logged> = mutableListOf()

    fun setServerAdapter(server: ServerAdapter) {
        Glass.server = server
    }

    fun setServerToken(uri: String, token: String, type: ServerType) {
        Glass.token = token

        // Initiate web socket connection to the api server
        log("Attempting to connect to Glass...")
        serverType = type
        socket = SocketHandler.connect(uri, token)
    }

    fun close() {
        SocketHandler.close()
    }

    fun log(message: String) {
        println("[Glass] $message")
    }

    fun sendLog(timestamp: String, log: Loggable, send: Boolean = false) {
        logs.add(Logged(timestamp, log))
        if (logs.size > 500) logs.removeAt(0)

        if (send) socket?.emit("CONSOLE_LOG", json.encodeToString(Logged(timestamp, log)))
    }

    fun updatePlayerList() {
        socket?.emit("PLAYER_LIST", json.encodeToString(server.getOnlinePlayers()))
    }

    enum class ServerType {
        SPIGOT, PAPER, BUNGEECORD, WATERFALL, VELOCITY, FORGE, FABRIC
    }
}