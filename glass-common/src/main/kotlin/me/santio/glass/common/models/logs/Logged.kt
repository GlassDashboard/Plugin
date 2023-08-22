package me.santio.glass.common.models.logs

import kotlinx.serialization.Serializable

@Serializable
data class Logged(
    val timestamp: String,
    val log: me.santio.glass.common.models.logs.Loggable
)
