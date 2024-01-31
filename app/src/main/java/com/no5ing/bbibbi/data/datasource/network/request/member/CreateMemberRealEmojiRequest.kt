package com.no5ing.bbibbi.data.datasource.network.request.member

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateMemberRealEmojiRequest(
    val type: String,
    val imageUrl: String,
) : Parcelable, BaseModel()