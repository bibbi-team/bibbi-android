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
        "CM0002" -> getString(R.string.server_invalid_input)
        "PO0001" -> getString(R.string.server_not_uploadable_time)
        "PO0002" -> getString(R.string.server_already_uploaded)
        "AU0002" -> getString(R.string.server_no_permission)
        "MB0001" -> getString(R.string.server_member_not_found)
        "FM0002" -> getString(R.string.server_already_in_family)
        "FM0001" -> getString(R.string.server_family_not_found)
        "DL0001" -> getString(R.string.server_link_not_valid)
        else -> getString(R.string.server_unknown_error, errorCode ?: "UN0001")
    }
}