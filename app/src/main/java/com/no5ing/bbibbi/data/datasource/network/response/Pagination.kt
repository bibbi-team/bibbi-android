package com.no5ing.bbibbi.data.datasource.network.response

import com.no5ing.bbibbi.data.model.BaseModel

data class Pagination<T : BaseModel>(
    val currentPage: Int,
    val totalPage: Int,
    val itemPerPage: Int,
    val hasNext: Boolean,
    val results: List<T>
)
