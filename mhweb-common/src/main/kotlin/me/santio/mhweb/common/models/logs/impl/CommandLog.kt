package me.santio.mhweb.common.models.logs.impl

import kotlinx.serialization.Serializable
import me.santio.mhweb.common.models.logs.Loggable

@Serializable
class CommandLog(
    val user: String,
    val command: String
): Loggable