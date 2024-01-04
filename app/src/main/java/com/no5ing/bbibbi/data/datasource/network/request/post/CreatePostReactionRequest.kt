package com.no5ing.bbibbi.data.datasource.network.request.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePostReactionRequest(
    val content: String,
) : Parcelable, BaseModel()

