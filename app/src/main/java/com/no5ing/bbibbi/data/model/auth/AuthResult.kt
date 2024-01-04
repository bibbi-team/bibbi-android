package com.no5ing.bbibbi.data.model.auth

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val isTemporaryToken: Boolean,
) : Parcelable, BaseModel()
