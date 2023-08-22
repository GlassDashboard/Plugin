package me.santio.glass.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class ConsoleHistory(
    val logs: List<me.santio.glass.common.models.logs.Logged>
)