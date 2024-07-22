package com.no5ing.bbibbi.data.model.family

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Family(
    val familyId: String,
    val familyName: String?,
    val familyNameEditorId: String?,
    val createdAt: ZonedDateTime,
) : Parcelable, BaseModel() {
    fun hasName() = !familyName.isNullOrEmpty()
}
