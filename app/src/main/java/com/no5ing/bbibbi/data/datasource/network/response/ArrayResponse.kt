package com.no5ing.bbibbi.data.datasource.network.response

import com.no5ing.bbibbi.data.model.BaseModel

data class ArrayResponse<T : BaseModel>(
    val results: List<T>
)
