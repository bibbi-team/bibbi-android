package com.no5ing.bbibbi.data.datasource.network.request.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePostRequest(
    val imageUrl: String,
    val content: String,
    val uploadTime: String,
) : Parcelable, BaseModel()
