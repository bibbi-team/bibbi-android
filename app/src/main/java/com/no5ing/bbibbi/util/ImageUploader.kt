package com.no5ing.bbibbi.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun OkHttpClient.uploadImage(
    targetFile: File,
    targetUrl: String
): String? {
    val destinationUrl = removeQueryParams(targetUrl)
    val request = Request.Builder()
        .url(targetUrl)
        .put(targetFile.asRequestBody("image/jpeg".toMediaType()))
        .build()
    val response = newCall(request).execute()
    return if (response.isSuccessful) {
        destinationUrl
    } else {
        null
    }
}