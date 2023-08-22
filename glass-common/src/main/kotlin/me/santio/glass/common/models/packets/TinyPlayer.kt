package me.santio.glass.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class TinyPlayer(
    val name: String,
    val uuid: String,
    val opped: Boolean,
    val whitelisted: Boolean,
    val online: Boolean
)