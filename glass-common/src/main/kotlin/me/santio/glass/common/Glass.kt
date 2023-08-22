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
    lateinit var server: me.santio.glass.common.adapter.ServerAdapter

    val json = Json {
        serializersModule = SerializersModule {
            polymorphic(me.santio.glass.common.models.logs.Loggable::class) {
                subclass(me.santio.glass.common.models.logs.impl.CommandLog::class)
                subclass(me.santio.glass.common.models.logs.impl.ConsoleLog::class)
            }
        }
    }

    var token: String? = null
    var serverType: me.santio.glass.common.Glass.ServerType = me.santio.glass.common.Glass.ServerType.SPIGOT
    var socket: Socket? = null

    val logs: MutableList<me.santio.glass.common.models.logs.Logged> = mutableListOf()

    fun setServerAdapter(server: me.santio.glass.common.adapter.ServerAdapter) {
        me.santio.glass.common.Glass.server = server
    }

    fun setServerToken(uri: String, token: String, type: me.santio.glass.common.Glass.ServerType) {
        me.santio.glass.common.Glass.token = token

        // Initiate web socket connection to the api server
        me.santio.glass.common.Glass.log("Attempting to connect to Glass...")
        me.santio.glass.common.Glass.serverType = type
        me.santio.glass.common.Glass.socket = me.santio.glass.common.socket.SocketHandler.connect(uri, token)
    }

    fun close() {
        me.santio.glass.common.socket.SocketHandler.close()
    }

    fun log(message: String) {
        println("[Glass] $message")
    }

    fun sendLog(timestamp: String, log: me.santio.glass.common.models.logs.Loggable, send: Boolean = false) {
        me.santio.glass.common.Glass.logs.add(me.santio.glass.common.models.logs.Logged(timestamp, log))
        if (me.santio.glass.common.Glass.logs.size > 500) me.santio.glass.common.Glass.logs.removeAt(0)

        if (send) me.santio.glass.common.Glass.socket?.emit("console:log", me.santio.glass.common.Glass.json.encodeToString(
            me.santio.glass.common.models.logs.Logged(timestamp, log)
        ))
    }

    fun updateCount(track: me.santio.glass.common.models.TrackedCount) {
        when (track) {
            me.santio.glass.common.models.TrackedCount.ONLINE_PLAYERS -> me.santio.glass.common.Glass.socket?.emit("PLAYER_LIST", me.santio.glass.common.Glass.json.encodeToString(
                me.santio.glass.common.Glass.server.getOnlinePlayers()))
            me.santio.glass.common.models.TrackedCount.ADMINISTRATOR_PLAYERS -> me.santio.glass.common.Glass.socket?.emit("ADMINISTRATOR_LIST", me.santio.glass.common.Glass.json.encodeToString(
                me.santio.glass.common.Glass.server.getServerAdministrators()))
            me.santio.glass.common.models.TrackedCount.WHITELISTED_PLAYERS -> me.santio.glass.common.Glass.socket?.emit("WHITELIST", me.santio.glass.common.Glass.json.encodeToString(
                me.santio.glass.common.Glass.server.getWhitelistedPlayers()))
            me.santio.glass.common.models.TrackedCount.BLACKLISTED_PLAYERS -> me.santio.glass.common.Glass.socket?.emit("BLACKLIST", me.santio.glass.common.Glass.json.encodeToString(
                me.santio.glass.common.Glass.server.getBannedPlayers()))
            me.santio.glass.common.models.TrackedCount.ALL -> {
                me.santio.glass.common.models.TrackedCount.values()
                    .filter{ it != track }
                    .forEach { me.santio.glass.common.Glass.updateCount(it) }
            }
        }
    }

    /**
     * Represents all supported server types that glass can recognize
     */
    enum class ServerType(val root: me.santio.glass.common.Glass.ServerType.Root? = null) {
        SPIGOT(me.santio.glass.common.Glass.ServerType.Root.BUKKIT),
        PAPER(me.santio.glass.common.Glass.ServerType.Root.BUKKIT),
        PURPUR(me.santio.glass.common.Glass.ServerType.Root.BUKKIT),
        BUNGEECORD,
        WATERFALL,
        VELOCITY,
        FORGE,
        FABRIC,
        ;

        companion object {
            fun fromRoot(root: me.santio.glass.common.Glass.ServerType.Root): Set<me.santio.glass.common.Glass.ServerType> {
                return values().filter { it.root == root }.toSet()
            }
        }

        enum class Root {
            BUKKIT,
            ;
        }
    }
}