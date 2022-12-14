package me.santio.mhweb.common.models.packets

import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    var name: String,
    var directory: Boolean,
    var accessible: Boolean,
    var size: Long? = null,
    var content: String? = null,
    var children: List<FileData>? = null,
    var error: String? = null
)
