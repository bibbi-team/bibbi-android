package com.no5ing.bbibbi.data.model.auth

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SocialLoginRequest(
    val accessToken: String,
) : Parcelable, BaseModel()
