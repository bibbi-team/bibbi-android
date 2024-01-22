package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalendarBanner(
    val familyTopPercentage: Int,
    val allFamilyMembersUploadedDays: Int,
) : Parcelable, BaseModel()
