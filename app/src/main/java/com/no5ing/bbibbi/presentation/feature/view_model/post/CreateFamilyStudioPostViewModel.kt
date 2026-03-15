package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.loading
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.no5ing.bbibbi.util.getZonedDateTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateFamilyStudioPostViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<AIPost>>() {
    override fun initState(): APIResponse<AIPost> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val imageUri = arguments.get("imageUrl") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            setState(loading())
            Timber.d("imageUri: $imageUri")

            val postResult = restAPI.getPostApi().createAiPost(
                CreatePostRequest(
                    imageUrl = imageUri,
                    content = "",
                    uploadTime = getZonedDateTimeString(),
                ),
                type = arguments.get("type"),
                aiPostType = arguments.get("aiPostType")?.uppercase(),
            ).wrapToAPIResponse()
            setState(postResult)
        }
    }

}