package com.no5ing.bbibbi.presentation.viewmodel.post

import android.net.Uri
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.getZonedDateTimeString
import com.no5ing.bbibbi.util.uploadImage
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val client: OkHttpClient,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Post>>() {
    override fun initState(): APIResponse<Post> {
        return APIResponse.loading()
    }

    fun saveTemporaryUri(uri: Uri) {
        localDataStorage.setTemporaryUri(uri.toString())
    }

    fun clearTemporaryUri() {
        localDataStorage.clearTemporaryUri()
    }

    override fun invoke(arguments: Arguments) {
        val imageUri = arguments.get("imageUri") ?: throw RuntimeException()
        val content = arguments.get("content") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            Timber.d("imageUri: $imageUri")

            val file = File(Uri.parse(imageUri).path!!)
            Timber.d("fileName : ${file.name}")
            val uploadRequest = restAPI.getPostApi().getUploadPostImageRequest(
                ImageUploadRequest(
                    imageName = file.name,
                )
            )
            uploadRequest.suspendOnSuccess {
                val imageUploadResult = client.uploadImage(
                    file,
                    data.url
                )
                if (imageUploadResult == null) {
                    //TODO: SOME KIND
                    return@suspendOnSuccess
                }
                val postResult = restAPI.getPostApi().createPost(
                    CreatePostRequest(
                        imageUrl = imageUploadResult,
                        content = content,
                        uploadTime = getZonedDateTimeString(),
                    )
                ).wrapToAPIResponse()
                setState(postResult)
            }
        }
    }

}