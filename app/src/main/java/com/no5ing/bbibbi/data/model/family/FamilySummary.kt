package com.no5ing.bbibbi.data.model.family

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FamilySummary(
    val totalImageCnt: Int,
) : Parcelable, BaseModel()
