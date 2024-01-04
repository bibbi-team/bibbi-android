package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostReactionSummary(
    val postId: String,
    val results: List<PostReactionSummaryElement>
) : Parcelable, BaseModel()

@Parcelize
data class PostReactionSummaryElement(
    val emojiType: String,
    val count: Int,
) : Parcelable, BaseModel()
