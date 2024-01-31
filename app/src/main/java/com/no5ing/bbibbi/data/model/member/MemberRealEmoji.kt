package com.no5ing.bbibbi.data.model.member

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemberRealEmoji(
    val realEmojiId: String,
    val type: String,
    val imageUrl: String,
) : Parcelable, BaseModel()
