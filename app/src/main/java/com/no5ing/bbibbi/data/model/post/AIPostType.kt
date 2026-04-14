package com.no5ing.bbibbi.data.model.post

import com.no5ing.bbibbi.data.model.BaseModel

data class AIPostType(
    val aiPostType: String,
    val imageUrl: String,
    val startDate: String,
    val aiPostTheme: String,
    val endDate: String,
    val postCount: Int,
) : BaseModel() {
    fun getTypeName(): String {
        return aiPostType
    }
}
