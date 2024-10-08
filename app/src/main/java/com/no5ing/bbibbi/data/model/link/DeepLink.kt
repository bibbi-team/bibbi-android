package com.no5ing.bbibbi.data.model.link

import android.os.Parcelable
import com.no5ing.bbibbi.data.model.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeepLink(
    val linkId: String,
    val url: String,
    val type: String,
    val details: Map<String, String>
) : Parcelable, BaseModel() {
    companion object {
        fun mock() = DeepLink(
            linkId = "linkId",
            url = "https://bibbi.app/o/sdo3032k3f",
            type = "type",
            details = mapOf("key" to "value")
        )
    }
}
