package me.santio.glass.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class CDNMetadata(
    val access_token: String,
    val created_at: Long,
    val path: String,
    val id: String,
    val name: String,
    val ttl: Long,
    val url: String
)