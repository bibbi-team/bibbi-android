package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DeletePostCommentViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<DefaultResponse>>() {
    override fun initState(): APIResponse<DefaultResponse> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        val commentId = arguments.get("commentId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            val res = restAPI.getPostApi().deletePostComment(
                postId = postId,
                commentId = commentId,
            )
            setState(res.wrapToAPIResponse())
        }
    }

}