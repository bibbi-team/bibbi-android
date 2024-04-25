package com.no5ing.bbibbi.data.model.mission

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Mission(
    val id: String,
    val date: LocalDate,
    val content: String,
) : Parcelable, BaseModel()
