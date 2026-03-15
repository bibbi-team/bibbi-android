package com.no5ing.bbibbi.data.model.post

import com.no5ing.bbibbi.data.model.BaseModel

data class AIPostType(
    val aiPostType: String,
    val imageUrl: String,
    val startDate: String,
    val name: String?,
    val endDate: String,
    val postCount: Int,
) : BaseModel() {
    fun getTypeName(): String {
        if (name != null) {
            return name
        }
        return when (aiPostType.lowercase()) {
            "chuseok_2025" -> "추석"
            "christmas_2025" -> "크리스마스"
            else -> "알 수 없는 유형"
        }
    }
}
