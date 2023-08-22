package me.santio.glass.common.models.logs.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.santio.glass.common.models.logs.Loggable

@Serializable
@SerialName("command")
class CommandLog(
    val user: String,
    val command: String
): me.santio.glass.common.models.logs.Loggable