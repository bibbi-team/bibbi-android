package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.ZonedDateTime

@Parcelize
data class DailyCalendarElement(
    val postId: String,
    val type: PostType,
    val date: LocalDate,
    val postImgUrl: String,
    val postContent: String,
    val missionContent: String?,
    val authorId: String,
    val commentCount: Int,
    val emojiCount: Int,
    val allFamilyMembersUploaded: Boolean,
    val createdAt: ZonedDateTime,
) : Parcelable, BaseModel() {
    fun toPost() = Post(
        postId = postId,
        authorId = authorId,
        type = type,
        missionId = null,
        commentCount = commentCount,
        emojiCount = emojiCount,
        imageUrl = postImgUrl,
        content = postContent,
        createdAt = createdAt,
    )
}