package com.no5ing.bbibbi.presentation.feature.view_model.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostCommentRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.PostComment
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CreatePostCommentViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<PostComment>>() {
    override fun initState(): APIResponse<PostComment> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        val content = arguments.get("content") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            val res = restAPI.getPostApi().createPostComment(
                postId = postId,
                body = CreatePostCommentRequest(
                    content = content,
                )
            )
            setState(res.wrapToAPIResponse())
        }
    }

}