package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class PostComment(
    val commentId: String,
    val postId: String,
    val type: PostCommentType,
    val memberId: String,
    val comment: String,
    val createdAt: ZonedDateTime,
) : Parcelable, BaseModel()
