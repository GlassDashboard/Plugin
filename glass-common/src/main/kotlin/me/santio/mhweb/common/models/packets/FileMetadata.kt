package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class FileMetadata(
    val name: String,
    val path: String,
    val directory: Boolean,
    val size: Long,
    val lastModified: Long,
    val content: String?,
    val children: List<FileMetadata>
)
