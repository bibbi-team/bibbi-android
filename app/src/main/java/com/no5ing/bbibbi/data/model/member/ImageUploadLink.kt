package com.no5ing.bbibbi.data.model.member

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUploadLink(
    val url: String,
) : Parcelable, BaseModel()