package me.santio.glass.common.socket

abstract class SocketEvent(
    val name: String
) {

    abstract fun onEvent(vararg data: Any)

}