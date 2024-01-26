package com.no5ing.bbibbi.presentation.feature.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostReactionRequest
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AddPostReactionViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Unit>() {
    override fun initState(): Unit {
        return
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.resourceId ?: throw RuntimeException()
        val emoji = arguments.get("emoji") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            restAPI.getPostApi().createPostReactions(
                postId = postId,
                body = CreatePostReactionRequest(
                    content = emoji,
                )
            )
        }
    }

}