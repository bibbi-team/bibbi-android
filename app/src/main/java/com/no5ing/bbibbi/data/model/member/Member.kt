package com.no5ing.bbibbi.data.model.member

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    val memberId: String,
    val name: String,
    val imageUrl: String?,
    val familyId: String?,
    val dayOfBirth: String,
) : Parcelable, BaseModel() {
    fun hasFamily(): Boolean = familyId != null

    companion object {
        fun unknown(): Member = Member(
            memberId = "unknown",
            name = "unknown",
            imageUrl = null,
            familyId = null,
            dayOfBirth = "unknown"
        )
    }
}
