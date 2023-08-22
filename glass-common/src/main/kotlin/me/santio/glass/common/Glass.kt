package me.santio.glass.common

import io.socket.client.Socket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.santio.glass.common.models.logs.impl.CommandLog
import me.santio.glass.common.models.logs.impl.ConsoleLog
import me.santio.glass.common.socket.SocketHandler

object Glass {
    lateinit var server: me.santio.glass.common.adapter.ServerAdapter

    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(me.santio.glass.common.models.logs.Loggable::class) {
                subclass(CommandLog::class)
                subclass(ConsoleLog::class)
            }
        }
    }

    var token: String? = null
    var serverType: ServerType = ServerType.SPIGOT
    var socket: Socket? = null

    val logs: MutableList<me.santio.glass.common.models.logs.Logged> = mutableListOf()

    fun setServerAdapter(server: me.santio.glass.common.adapter.ServerAdapter) {
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

    fun sendLog(timestamp: String, log: me.santio.glass.common.models.logs.Loggable, send: Boolean = false) {
        logs.add(me.santio.glass.common.models.logs.Logged(timestamp, log))
        if (logs.size > 500) logs.removeAt(0)

        if (send) socket?.emit(
            "console:log", json.encodeToString(
                me.santio.glass.common.models.logs.Logged(timestamp, log)
            )
        )
    }

    fun updateCount(track: me.santio.glass.common.models.TrackedCount) {
        when (track) {
            me.santio.glass.common.models.TrackedCount.ONLINE_PLAYERS -> socket?.emit(
                "PLAYER_LIST", json.encodeToString(
                    server.getOnlinePlayers()
                )
            )

            me.santio.glass.common.models.TrackedCount.ADMINISTRATOR_PLAYERS -> socket?.emit(
                "ADMINISTRATOR_LIST", json.encodeToString(
                    server.getServerAdministrators()
                )
            )

            me.santio.glass.common.models.TrackedCount.WHITELISTED_PLAYERS -> socket?.emit(
                "WHITELIST", json.encodeToString(
                    server.getWhitelistedPlayers()
                )
            )

            me.santio.glass.common.models.TrackedCount.BLACKLISTED_PLAYERS -> socket?.emit(
                "BLACKLIST", json.encodeToString(
                    server.getBannedPlayers()
                )
            )

            me.santio.glass.common.models.TrackedCount.ALL -> {
                me.santio.glass.common.models.TrackedCount.values()
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