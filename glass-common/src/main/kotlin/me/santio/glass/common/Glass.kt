package me.santio.glass.common

import io.socket.client.Socket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.santio.glass.common.adapter.ServerAdapter
import me.santio.glass.common.models.TrackedCount
import me.santio.glass.common.models.logs.Loggable
import me.santio.glass.common.models.logs.Logged
import me.santio.glass.common.models.logs.impl.CommandLog
import me.santio.glass.common.models.logs.impl.ConsoleLog
import me.santio.glass.common.socket.SocketHandler

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
        private set

    var serverType: ServerType = ServerType.SPIGOT
        private set

    var socket: Socket? = null
        internal set

    var api_uri: String? = null
        private set

    val logs: MutableList<Logged> = mutableListOf()

    fun setServerAdapter(server: ServerAdapter) {
        Glass.server = server
    }

    fun setServerToken(uri: String, ws: String, token: String, type: ServerType) {
        Glass.token = token

        // Initiate web socket connection to the api server
        log("Attempting to connect to Glass...")
        serverType = type
        api_uri = uri
        socket = SocketHandler.connect(ws, token)
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

        if (send) socket?.emit(
            "console:log", json.encodeToString(
                Logged(timestamp, log)
            )
        )
    }

    fun updateCount(track: TrackedCount) {
        when (track) {
            TrackedCount.ONLINE_PLAYERS -> socket?.emit(
                "PLAYER_LIST", json.encodeToString(
                    server.getOnlinePlayers()
                )
            )

            TrackedCount.ADMINISTRATOR_PLAYERS -> socket?.emit(
                "ADMINISTRATOR_LIST", json.encodeToString(
                    server.getServerAdministrators()
                )
            )

            TrackedCount.WHITELISTED_PLAYERS -> socket?.emit(
                "WHITELIST", json.encodeToString(
                    server.getWhitelistedPlayers()
                )
            )

            TrackedCount.BLACKLISTED_PLAYERS -> socket?.emit(
                "BLACKLIST", json.encodeToString(
                    server.getBannedPlayers()
                )
            )

            TrackedCount.ALL -> {
                TrackedCount.values()
                    .filter { it != track }
                    .forEach { updateCount(it) }
            }
        }
    }

    /**
     * Represents all supported server types that glass can recognize
     */
    enum class ServerType(val root: Root? = null) {
        SPIGOT(Root.BUKKIT),
        PAPER(Root.BUKKIT),
        PURPUR(Root.BUKKIT),
        BUNGEECORD,
        WATERFALL,
        VELOCITY,
        FORGE,
        FABRIC,
        ;

        companion object {
            fun fromRoot(root: Root): Set<ServerType> {
                return values().filter { it.root == root }.toSet()
            }
        }

        enum class Root {
            BUKKIT,
            ;
        }
    }
}