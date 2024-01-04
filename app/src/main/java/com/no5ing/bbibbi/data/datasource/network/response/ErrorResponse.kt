package com.no5ing.bbibbi.data.datasource.network.response

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse(
    val code: String,
    val message: String
) : Parcelable, BaseModel()