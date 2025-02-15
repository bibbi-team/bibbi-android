package com.no5ing.bbibbi.presentation.feature.view_model.post

import android.content.Context
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostCommentRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreateVoiceCommentRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.PostComment
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import com.no5ing.bbibbi.util.uploadVoiceComment
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateAudioCommentViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val client: OkHttpClient,
    private val context: Context,
) : BaseViewModel<APIResponse<PostComment>>() {
    override fun initState(): APIResponse<PostComment> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            val uploadRes = restAPI.getPostApi().voiceCommentUploadRequest(
                ImageUploadRequest(
                    imageName = "audio.aac",
                )
            )
            uploadRes.suspendOnSuccess {
                val file = File(context.cacheDir, "recording.aac")
                if (!file.exists()) {
                    Timber.d("file not exists")
                    setState(APIResponse.unknownError())
                    return@suspendOnSuccess
                }
                val commentUploadResult = client.uploadVoiceComment(
                    targetFile = file,
                    targetUrl = data.url
                )
                if (commentUploadResult == null) {
                    setState(APIResponse.unknownError())
                    return@suspendOnSuccess
                }
                val voiceCommentResult = restAPI.getPostApi().createVoiceComment(
                    postId = postId,
                    CreateVoiceCommentRequest(
                        fileUrl = commentUploadResult
                    )
                ).wrapToAPIResponse()
                setState(voiceCommentResult)
            }
        }
    }

}