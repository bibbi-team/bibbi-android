package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AIImageResponse(
    val imageUrl: String
): Parcelable, BaseModel()