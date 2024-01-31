package com.no5ing.bbibbi.data.repository

data class Arguments(
    val resourceId: String? = null,
    val arguments: Map<String, Any?> = emptyMap(),
) {
    fun get(key: String): String? {
        return arguments[key] as? String?
    }

    fun <T> getObject(key: String): T? {
        return arguments[key] as? T?
    }
}
