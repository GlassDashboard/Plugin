package me.santio.glass.common.socket

import io.socket.client.IO
import io.socket.client.Socket
import me.santio.glass.common.Glass
import me.santio.glass.common.models.packets.ResolvablePath
import org.json.JSONObject
import org.reflections.Reflections
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import kotlin.concurrent.schedule

object SocketHandler {

    private var connectionFailed = false

    fun connect(uri: String, token: String): Socket {
        me.santio.glass.common.Glass.log("Connecting to Glass WebSocket...")

        val auth = mapOf(
            "origin" to "server",
            "token" to token,
            "server_type" to me.santio.glass.common.Glass.serverType.name,
            "server_version" to me.santio.glass.common.Glass.server.getServerVersion(),
            "plugin_version" to me.santio.glass.common.Glass.server.getPluginVersion()
        )

        val socket =  IO.Options.builder()
            .setPath("/socket")
            .setAuth(auth)
            .setReconnectionAttempts(0)
            .build()
            .let { IO.socket(uri, it).connect() }

        me.santio.glass.common.Glass.socket = socket

        socket.on("connect") {
            me.santio.glass.common.Glass.log("Connected to Glass WebSocket!")
            me.santio.glass.common.socket.SocketHandler.connectionFailed = false
            me.santio.glass.common.socket.SocketHandler.loadEvents()
        }

        socket.on("connect_error") {
            if (!me.santio.glass.common.socket.SocketHandler.connectionFailed) {
                me.santio.glass.common.socket.SocketHandler.connectionFailed = true
                me.santio.glass.common.Glass.log("Failed to connect to Glass WebSocket!")
            }

            val error = it[0] as? JSONObject
            if (error != null) {
                me.santio.glass.common.Glass.log(error.getString("message"))

                if (error.getString("message") == "Invalid authentication data") {
                    me.santio.glass.common.Glass.log("Authentication data:")

                    for (key in auth.keys) {
                        me.santio.glass.common.Glass.log("$key: ${if (key == "token") "<hidden>" else auth[key]}")
                    }
                }
            }
        }

        // Backend goes offline, give it a bit to come back online before trying to go through reconnecting
        socket.on("disconnect") {
            me.santio.glass.common.Glass.log("Disconnected from Glass WebSocket!")
            socket.off()

            Timer("GlassReconnect", false).schedule(5000) {
                me.santio.glass.common.Glass.socket?.close()
                me.santio.glass.common.socket.SocketHandler.connect(uri, token)
            }
        }

        socket.on("reconnect") {
            me.santio.glass.common.Glass.log("Reconnected to Glass WebSocket!")
            me.santio.glass.common.socket.SocketHandler.connectionFailed = false
            me.santio.glass.common.socket.SocketHandler.loadEvents()
        }

        socket.on("reconnect_attempt") {
            me.santio.glass.common.Glass.log("Attempting to reconnect to Glass WebSocket...")
        }

        socket.on("reconnect_failed") {
            me.santio.glass.common.Glass.log("Failed to reconnect to Glass WebSocket!")
        }

        socket.on("reconnect_error") {
            me.santio.glass.common.Glass.log("Error while reconnecting to Glass WebSocket!")
        }

        socket.on("error") {
            me.santio.glass.common.Glass.log("[Error] ${it[0]}")
        }

        return socket
    }

    fun close() {
        me.santio.glass.common.Glass.socket?.close()
    }

    fun sendFile(room: String, file: File, callback: ((Unit) -> Unit)? = null) {
        var finished = false
        val reader = file.inputStream().buffered()
        val buffer = ByteArray(1 * 1024 * 1024)
        var read = reader.read(buffer)
        var offset = 0

        Timer().schedule(50, 100) {
            if (finished) {
                me.santio.glass.common.Glass.socket?.emit("EOF-$room")
                reader.close()
                callback?.invoke(Unit)
                cancel()
                return@schedule
            } else if (read == -1) {
                finished = true
                return@schedule
            }

            val bytes = buffer.copyOfRange(0, read)
            val byteBuffer = ByteBuffer.wrap(bytes)
            me.santio.glass.common.Glass.socket?.emit("BUFFER-$room", byteBuffer.array())
            offset += read
            read = reader.read(buffer)
        }
    }

    private fun loadEvents() {
        val reflections = Reflections("me.santio.glass.common.socket.impl")
        val events = reflections.getSubTypesOf(me.santio.glass.common.socket.SocketEvent::class.java)

        events.forEach { clazz ->
            val event = clazz.getDeclaredConstructor().newInstance()
            me.santio.glass.common.Glass.socket?.on(event.name) { event.onEvent(*it) }
        }
    }

    // Extensions
    fun JSONObject.toPath(): me.santio.glass.common.models.packets.ResolvablePath {
        return ResolvablePath(
            this.getString("path"),
            this.getBoolean("root")
        )
    }

}