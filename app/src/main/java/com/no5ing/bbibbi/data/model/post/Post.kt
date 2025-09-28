package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Post(
    val postId: String,
    val authorId: String,
    val type: PostType,
    val missionId: String?,
    val commentCount: Int,
    val emojiCount: Int,
    val imageUrl: String,
    val content: String,
    val createdAt: ZonedDateTime,
) : Parcelable, BaseModel()

@Parcelize
data class AIPost(
    val postId: String,
    val authorId: String,
    val type: PostType,
    val imageUrl: String,
    val authorName: String?,
    val authorImageUrl: String?,
    val createdAt: ZonedDateTime,
) : Parcelable, BaseModel()