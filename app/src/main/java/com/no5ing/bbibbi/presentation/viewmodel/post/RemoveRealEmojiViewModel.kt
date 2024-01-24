package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RemoveRealEmojiViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Unit>() {
    override fun initState(): Unit {
        return
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.resourceId ?: throw RuntimeException()
        val realEmojiId = arguments.get("realEmojiId") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            restAPI.getPostApi().deletePostRealEmojiFromPost(
                postId = postId,
                postRealEmojiId = realEmojiId,
            )
        }
    }

}