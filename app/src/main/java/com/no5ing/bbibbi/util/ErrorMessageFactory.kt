package com.no5ing.bbibbi.util

import android.content.res.Resources
import androidx.compose.runtime.Composable
import com.no5ing.bbibbi.R

@Composable
fun getErrorMessage(errorCode: String?): String {
    val resources = localResources()
    return resources.getErrorMessage(errorCode)
}

fun Resources.getErrorMessage(errorCode: String?): String {
    return when (errorCode) {
        "PO0002" -> getString(R.string.server_already_uploaded)
        else -> getString(R.string.server_unknown_error)
    }
}