package me.santio.glass.common.models.logs.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("command")
class CommandLog(
    val user: String,
    val command: String
) : me.santio.glass.common.models.logs.Loggable