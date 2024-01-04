package com.no5ing.bbibbi.data.model.family

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FamilySummary(
    val totalParticipateCnt: Int,
    val totalImageCnt: Int,
    val myImageCnt: Int,
) : Parcelable, BaseModel()
