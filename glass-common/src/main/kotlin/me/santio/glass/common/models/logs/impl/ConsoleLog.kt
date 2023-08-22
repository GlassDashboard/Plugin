package me.santio.glass.common.models.logs.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.santio.glass.common.models.logs.Loggable

@Serializable
@SerialName("console")
data class ConsoleLog(
    val log: String,
    val level: String,
): me.santio.glass.common.models.logs.Loggable
