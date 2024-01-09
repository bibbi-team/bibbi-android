package com.no5ing.bbibbi.data.model.widget

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetResult(
    val profileImageUrl: String,
    val postImageUrl: String,
    val postContent: String,
    val authorName: String,
) : Parcelable, BaseModel()