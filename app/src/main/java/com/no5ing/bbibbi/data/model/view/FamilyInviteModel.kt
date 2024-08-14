package com.no5ing.bbibbi.data.model.view

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FamilyInviteModel(
    val familyId: String,
    val familyName: String?,
    val familyMembersProfileImageUrls: List<String>,
    val familyMemberNames: List<String>,
    val extraFamilyMembersCount: Int,
    val familyMembersCount: Int,
): Parcelable, BaseModel()
