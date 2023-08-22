package me.santio.mhweb.common.models.packets

data class FileChanges(
    val path: String? = null,
    val copy: Boolean? = null,
    val content: String? = null,
    val unarchive: Boolean? = null
)
