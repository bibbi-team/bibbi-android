package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class PostRealEmoji(
    val postRealEmojiId: String,
    val postId: String,
    val memberId: String,
    val emojiType: String,
    val realEmojiId: String,
    val emojiImageUrl: String,
) : Parcelable, BaseModel()
