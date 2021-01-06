package com.jmr.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileResponse(
    @SerializedName(".tag") val tag: String,
    val name: String,
    val path_display: String,
    val id: String,
    val size: Int = 0,
    val client_modified: String?
) : Parcelable

@Parcelize
data class DropboxFileList(
    val entries: List<FileResponse>
) : Parcelable