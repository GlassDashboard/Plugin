package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable
import me.santio.mhweb.common.models.logs.Logged

@Serializable
data class ConsoleHistory(
    val logs: List<Logged>
)