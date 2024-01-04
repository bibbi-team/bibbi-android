package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostReactionBarViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<List<PostReactionUiState>>() {
    override fun initState(): List<PostReactionUiState> {
        return emptyList()
    }

    private fun reactMe(emoji: String) {
        val previousList = uiState.value.toMutableList()
        previousList.add(
            PostReactionUiState(
                reactionId = "temp",
                memberId = localDataStorage.getMe()?.memberId ?: throw RuntimeException(),
                emojiType = emoji,
                isMe = true,
            )
        )
        setState(previousList)
    }

    private fun unReactMe(emoji: String) {
        val previousList = uiState.value.toMutableList()
        previousList.removeIf {
            it.emojiType == emoji && it.isMe
        }
        setState(previousList)
    }

    fun toggleReact(emoji: String): Boolean {
        val isMeReacted = uiState.value.any {
            it.emojiType == emoji && it.isMe
        }
        if (isMeReacted) {
            unReactMe(emoji)
            return false
        } else {
            reactMe(emoji)
            return true
        }
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        setState(emptyList())
        viewModelScope.launch(Dispatchers.IO) {
            val me = localDataStorage.getMe()
            val reactions = restAPI
                .getPostApi()
                .getPostReactions(
                    postId = postId,
                )
            reactions.suspendOnSuccess {
                setState(data.results.map {
                    PostReactionUiState(
                        reactionId = it.reactionId,
                        memberId = it.memberId,
                        emojiType = it.emojiType,
                        isMe = it.memberId == me?.memberId
                    )
                })
            }
        }
    }

}