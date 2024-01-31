package com.no5ing.bbibbi.presentation.feature.uistate.post

import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member

abstract class PostReactionUiState(
    val reactionId: String,
    val memberId: String,
    val emojiType: String,
    val isMe: Boolean,
    val member: Member?,
) : BaseModel() {
    abstract fun getGroupKey(): String

    companion object {
        fun mock() = NormalPostReactionUiState(
            reactionId = "1",
            memberId = "1",
            emojiType = "1",
            isMe = true,
            member = Member.unknown(),
        )
    }
}

class NormalPostReactionUiState(
    reactionId: String,
    memberId: String,
    emojiType: String,
    isMe: Boolean,
    member: Member?,
) : PostReactionUiState(
    reactionId,
    memberId,
    emojiType,
    isMe,
    member,
) {
    override fun getGroupKey(): String {
        return emojiType
    }
}

class RealEmojiPostReactionUiState(
    val realEmojiId: String,
    val imageUrl: String,
    reactionId: String,
    memberId: String,
    emojiType: String,
    isMe: Boolean,
    member: Member?,
) : PostReactionUiState(
    reactionId,
    memberId,
    emojiType,
    isMe,
    member,
) {
    override fun getGroupKey(): String {
        return realEmojiId
    }
}