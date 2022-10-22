package me.santio.mhweb.common.models.logs

import kotlinx.serialization.Serializable

@Serializable
data class Logged(
    val timestamp: String,
    val log: Loggable
)
