package com.no5ing.bbibbi.data.model.member

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.util.isBirthdayNow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Member(
    val memberId: String,
    @JsonProperty("name")
    private val _name: String,
    val imageUrl: String?,
    val familyId: String?,
    val dayOfBirth: String,
) : Parcelable, BaseModel() {
    @IgnoredOnParcel
    val isBirthdayToday: Boolean = LocalDate
        .parse(dayOfBirth)
        .isBirthdayNow()

    val name: String
        get() = if(_name != deletedMemberName) _name else "알 수 없음"

    fun hasFamily(): Boolean = familyId != null

    fun hasProfileImage(): Boolean = !imageUrl.isNullOrBlank()

    companion object {
        const val deletedMemberName = "DeletedMember"
        fun unknown(): Member = Member(
            memberId = "unknown",
            _name = "DeletedMember",
            imageUrl = null,
            familyId = null,
            dayOfBirth = "2001-12-30"
        )
    }
}
