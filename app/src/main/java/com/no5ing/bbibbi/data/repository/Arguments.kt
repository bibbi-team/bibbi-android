package com.no5ing.bbibbi.data.repository

data class Arguments(
    val resourceId: String? = null,
    val arguments: Map<String, String?> = emptyMap(),
) {
    fun get(key: String): String? {
        return arguments[key]
    }
}
