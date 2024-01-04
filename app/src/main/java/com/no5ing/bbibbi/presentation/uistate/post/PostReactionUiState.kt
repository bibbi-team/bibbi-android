package com.no5ing.bbibbi.presentation.uistate.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostReactionUiState(
    val reactionId: String,
    val memberId: String,
    val emojiType: String,
    val isMe: Boolean,
) : Parcelable, BaseModel()
