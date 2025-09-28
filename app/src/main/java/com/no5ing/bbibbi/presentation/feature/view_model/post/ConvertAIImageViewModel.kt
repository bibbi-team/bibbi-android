package com.no5ing.bbibbi.presentation.feature.view_model.post

import android.net.Uri
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.AIImageResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ConvertAIImageViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<AIImageResponse>>() {
    override fun initState(): APIResponse<AIImageResponse> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val imageUri = arguments.get("imageUri") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            setState(loading())
            Timber.d("imageUri: $imageUri")

            val file = File(Uri.parse(imageUri).path!!)
            Timber.d("fileName : ${file.name}")

            val aiImage = restAPI.getPostApi().convertImage(
                MultipartBody.Part.createFormData(
                    name = "image",
                    filename = file.name,
                    body = file.asRequestBody("image/jpeg".toMediaType()),
                )
            ).wrapToAPIResponse()
            setState(aiImage)
        }
    }

}