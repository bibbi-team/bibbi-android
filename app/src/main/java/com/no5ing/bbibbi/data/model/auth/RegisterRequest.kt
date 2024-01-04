package com.no5ing.bbibbi.data.model.auth

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterRequest(
    val memberName: String,
    val dayOfBirth: String,
    val profileImgUrl: String?,
) : Parcelable, BaseModel()
