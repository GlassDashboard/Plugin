package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class Command(
    val user: String,
    val original: String,
    val command: String
)