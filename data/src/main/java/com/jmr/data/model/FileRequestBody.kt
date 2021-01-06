package com.jmr.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileRequestBody(
    val path: String
):Parcelable