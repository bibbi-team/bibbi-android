package com.no5ing.bbibbi.data.model.post

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AIImageCount(
    val familyAiImageCount: Int,
    val availableAiImageCount: Int,
): Parcelable, BaseModel() {
    fun hasAvailableImage(): Boolean {
        return availableAiImageCount > 0
    }
}