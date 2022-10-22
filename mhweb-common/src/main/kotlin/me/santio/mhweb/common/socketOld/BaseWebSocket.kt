@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package me.santio.mhweb.common.socketOld

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CompletionStage
import kotlin.concurrent.schedule

abstract class BaseWebSocket(
    private val uri: String,
    val autoReconnect: Boolean = false,
    val streamBinary: Boolean = false
): WebSocket.Listener {

    init { connect(uri) }
    private var socket: WebSocket? = null
    val gson = Gson()

    fun getSocket(): WebSocket? {
        return socket
    }

    private fun connect(uri: String) {
        val client = HttpClient.newHttpClient()
        client.newWebSocketBuilder()
            .buildAsync(URI.create(uri), this)
    }

    fun reconnect() {
        connect(uri)
    }

    fun close(code: Int = 1000) {
        getSocket()?.sendClose(code, "Closed by client")
    }

    fun send(message: String) {
        getSocket()?.sendText(message, true)
    }

    fun send(message: Any, gson: Gson = this.gson) {
        send(gson.toJson(message))
    }

    fun send(bytes: ByteBuffer) {
        getSocket()?.sendBinary(bytes, true)
    }

    fun send(file: File, message: Any? = null) {
        if (message != null) send(message)
        val byteBuffer = ByteBuffer.wrap(file.readBytes())
        var finished = false

        Timer().schedule(0, 50) {
            if (finished) {
                send("{\"type\": \"EOF\"}")
                cancel()
                return@schedule
            } else if (!byteBuffer.hasRemaining()) {
                finished = true
                return@schedule
            }

            val slice = byteBuffer.slice()
            slice.limit(slice.capacity().coerceAtMost(1024 * 1024))
            byteBuffer.position(byteBuffer.position() + slice.limit())
            send(slice)
        }
    }

    override fun onOpen(webSocket: WebSocket) {
        socket = webSocket
        webSocket.request(1)
        this.onConnect()
    }

    private var buffer: ByteArrayOutputStream = ByteArrayOutputStream()
    override fun onBinary(webSocket: WebSocket, data: ByteBuffer, last: Boolean): CompletionStage<*>? {
        // Make copy of the read-only buffer and make it writable
        data.rewind()
        val array = ByteArray(data.remaining())
        val copy = ByteBuffer.wrap(array)
        copy.put(data)
        copy.flip()

        if (streamBinary) {
            this.onBinaryStream(data, last)
            return super.onBinary(webSocket, data, last)
        }

        buffer.write(copy.array())

        if (last) {
            this.onBinary(ByteBuffer.wrap(buffer.toByteArray()))
            buffer = ByteArrayOutputStream()
        }

        return super.onBinary(webSocket, data, last)
    }

    override fun onClose(webSocket: WebSocket, code: Int, reason: String): CompletionStage<*>? {
        this.onClose(code, reason)
        return null
    }

    override fun onError(webSocket: WebSocket, exception: Throwable) {
        this.onError(exception)

        if (autoReconnect) Timer().schedule(10000) {
            reconnect()
        }
    }

    var batch: String = ""
    override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
        batch += data.toString()
        if (last) {
            this.onMessage(batch)
            batch = ""
        }
        return super.onText(socket, data, last)
    }

    open fun onMessage(message: String) { /* do nothing */ }
    open fun onBinary(bytes: ByteBuffer) { /* do nothing */ }
    open fun onBinaryStream(bytes: ByteBuffer, last: Boolean) { /* do nothing */ }
    open fun onClose(code: Int, reason: String) { /* do nothing */ }
    open fun onError(exception: Throwable) { /* do nothing */ }
    open fun onConnect() { /* do nothing */ }

    abstract class Json<T>(
        uri: String,
        val baseClass: Class<T>,
        autoReconnect: Boolean = false
    ): BaseWebSocket(uri, autoReconnect) {

        override fun onMessage(message: String) {
            try {
                val json = gson.fromJson(message, baseClass)
                onJson(json, message)
            } catch(e: JsonSyntaxException) {
                this.onJsonParseError(e, message)
            }
        }

        abstract fun onJson(json: T, raw: String)
        open fun onJsonParseError(exception: Exception, message: String) { /* do nothing */ }

    }
}