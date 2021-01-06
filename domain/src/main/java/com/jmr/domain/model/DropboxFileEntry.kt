package com.jmr.domain.model

import com.google.gson.annotations.SerializedName

data class DropboxFileEntry(
    @SerializedName(".tag") val tag: String,
    val name: String,
    val path_display: String,
    val id: String,
    val size: Int = 0,
    val client_modified: String?
)

data class DropboxFileList(
    val entries: List<DropboxFileEntry>
)