package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.post.DeletePostReactionRequest
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemovePostReactionViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Unit>() {
    override fun initState(): Unit {
        return
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.resourceId ?: throw RuntimeException()
        val emoji = arguments.get("emoji") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            restAPI.getPostApi().deletePostReactions(
                postId = postId,
                body = DeletePostReactionRequest(
                    content = emoji,
                )
            )
        }
    }

}