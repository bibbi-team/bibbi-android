package com.no5ing.bbibbi.presentation.uistate.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostReactionUiState(
    val reactionId: String,
    val memberId: String,
    val emojiType: String,
    val isMe: Boolean,
    val member: Member?,
) : Parcelable, BaseModel()
