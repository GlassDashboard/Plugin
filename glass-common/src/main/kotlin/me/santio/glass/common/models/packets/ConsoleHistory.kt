package me.santio.glass.common.models.packets

import kotlinx.serialization.Serializable
import me.santio.glass.common.models.logs.Logged

@Serializable
data class ConsoleHistory(
    val logs: List<me.santio.glass.common.models.logs.Logged>
)