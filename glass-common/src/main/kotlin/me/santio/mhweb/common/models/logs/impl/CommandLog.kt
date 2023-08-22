package me.santio.mhweb.common.models.logs.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.santio.mhweb.common.models.logs.Loggable

@Serializable
@SerialName("command")
class CommandLog(
    val user: String,
    val command: String
): Loggable