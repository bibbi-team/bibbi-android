package com.no5ing.bbibbi.presentation.feature.uistate.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.PostCommentType
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class PostCommentUiState(
    val postId: String,
    val commentId: String,
    val memberId: String,
    val type: PostCommentType,
    val createdAt: ZonedDateTime,
    val content: String,
    val member: Member?,
) : Parcelable, BaseModel()
