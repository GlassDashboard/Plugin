package me.santio.mhweb.common.socket

import io.socket.client.IO
import io.socket.client.Socket
import me.santio.mhweb.common.Glass
import me.santio.mhweb.common.models.packets.ResolvablePath
import org.json.JSONObject
import org.reflections.Reflections
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import kotlin.concurrent.schedule

object SocketHandler {

    private var connectionFailed = false

    fun connect(uri: String, token: String): Socket {
        Glass.log("Connecting to Glass WebSocket...")

        val auth = mapOf(
            "origin" to "server",
            "token" to token,
            "server_type" to Glass.serverType.name,
            "server_version" to Glass.server.getServerVersion(),
            "plugin_version" to Glass.server.getPluginVersion()
        )

        val socket =  IO.Options.builder()
            .setPath("/socket")
            .setAuth(auth)
            .setReconnectionAttempts(0)
            .build()
            .let { IO.socket(uri, it).connect() }

        Glass.socket = socket

        socket.on("connect") {
            Glass.log("Connected to Glass WebSocket!")
            connectionFailed = false
            loadEvents()
        }

        socket.on("connect_error") {
            if (!connectionFailed) {
                connectionFailed = true
                Glass.log("Failed to connect to Glass WebSocket!")
            }

            val error = it[0] as? JSONObject
            if (error != null) {
                Glass.log(error.getString("message"))

                if (error.getString("message") == "Invalid authentication data") {
                    Glass.log("Authentication data:")

                    for (key in auth.keys) {
                        Glass.log("$key: ${if (key == "token") "<hidden>" else auth[key]}")
                    }
                }
            }
        }

        // Backend goes offline, give it a bit to come back online before trying to go through reconnecting
        socket.on("disconnect") {
            Glass.log("Disconnected from Glass WebSocket!")
            socket.off()

            Timer("GlassReconnect", false).schedule(5000) {
                Glass.socket?.close()
                connect(uri, token)
            }
        }

        socket.on("reconnect") {
            Glass.log("Reconnected to Glass WebSocket!")
            connectionFailed = false
            loadEvents()
        }

        socket.on("reconnect_attempt") {
            Glass.log("Attempting to reconnect to Glass WebSocket...")
        }

        socket.on("reconnect_failed") {
            Glass.log("Failed to reconnect to Glass WebSocket!")
        }

        socket.on("reconnect_error") {
            Glass.log("Error while reconnecting to Glass WebSocket!")
        }

        socket.on("error") {
            Glass.log("[Error] ${it[0]}")
        }

        return socket
    }

    fun close() {
        Glass.socket?.close()
    }

    fun sendFile(room: String, file: File, callback: ((Unit) -> Unit)? = null) {
        var finished = false
        val reader = file.inputStream().buffered()
        val buffer = ByteArray(1 * 1024 * 1024)
        var read = reader.read(buffer)
        var offset = 0

        Timer().schedule(50, 100) {
            if (finished) {
                Glass.socket?.emit("EOF-$room")
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
            Glass.socket?.emit("BUFFER-$room", byteBuffer.array())
            offset += read
            read = reader.read(buffer)
        }
    }

    private fun loadEvents() {
        val reflections = Reflections("me.santio.mhweb.common.socket.impl")
        val events = reflections.getSubTypesOf(SocketEvent::class.java)

        events.forEach { clazz ->
            val event = clazz.getDeclaredConstructor().newInstance()
            Glass.socket?.on(event.name) { event.onEvent(*it) }
        }
    }

    // Extensions
    fun JSONObject.toPath(): ResolvablePath {
        return ResolvablePath(
            this.getString("path"),
            this.getBoolean("root")
        )
    }

}