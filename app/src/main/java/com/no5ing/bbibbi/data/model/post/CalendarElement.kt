package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarElement(
    val date: String,
    val representativePostId: String,
    val representativeThumbnailUrl: String,
    val allFamilyMembersUploaded: Boolean,
) : Parcelable, BaseModel()
