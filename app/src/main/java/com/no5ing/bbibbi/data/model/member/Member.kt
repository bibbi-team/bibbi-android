package com.no5ing.bbibbi.data.model.member

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import com.no5ing.bbibbi.util.isBirthdayNow
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.time.LocalDate

@Parcelize
data class Member(
    val memberId: String,
    val name: String,
    val imageUrl: String?,
    val familyId: String?,
    val dayOfBirth: String,
) : Parcelable, BaseModel() {
    @IgnoredOnParcel
    val isBirthdayToday: Boolean = LocalDate
        .parse(dayOfBirth)
        .isBirthdayNow()

    fun hasFamily(): Boolean = familyId != null

    companion object {
        fun unknown(): Member = Member(
            memberId = "unknown",
            name = "unknown",
            imageUrl = null,
            familyId = null,
            dayOfBirth = "2001-12-30"
        )
    }
}
