package com.no5ing.bbibbi.data.model.auth

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppVersion(
    val appKey: String,
    val appVersion: String,
    val latest: Boolean,
    val inReview: Boolean,
    val inService: Boolean,
) : Parcelable, BaseModel()
