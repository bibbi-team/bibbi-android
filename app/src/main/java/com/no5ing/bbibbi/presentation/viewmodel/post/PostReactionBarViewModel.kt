package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.parallelMap
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostReactionBarViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val memberCacheProvider: MemberCacheProvider,
) : BaseViewModel<List<PostReactionUiState>>() {
    override fun initState(): List<PostReactionUiState> {
        return emptyList()
    }

    private fun reactMe(memberId: String, emoji: String) {
        val previousList = uiState.value.toMutableList()
        viewModelScope.launch {
            previousList.add(
                PostReactionUiState(
                    reactionId = "temp",
                    memberId = memberId,
                    emojiType = emoji,
                    isMe = true,
                    member = memberCacheProvider.getMember(memberId)
                )
            )
            setState(previousList)
        }
    }

    private fun unReactMe(emoji: String) {
        val previousList = uiState.value.toMutableList()
        previousList.removeIf {
            it.emojiType == emoji && it.isMe
        }
        setState(previousList)
    }

    fun toggleReact(memberId: String, emoji: String): Boolean {
        val isMeReacted = uiState.value.any {
            it.emojiType == emoji && it.isMe
        }
        if (isMeReacted) {
            unReactMe(emoji)
            return false
        } else {
            reactMe(memberId, emoji)
            return true
        }
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        setState(emptyList())
        withMutexScope(Dispatchers.IO) {
            val reactions = restAPI
                .getPostApi()
                .getPostReactions(
                    postId = postId,
                )
            reactions.suspendOnSuccess {
                setState(data.results.parallelMap {
                    PostReactionUiState(
                        reactionId = it.reactionId,
                        memberId = it.memberId,
                        emojiType = it.emojiType,
                        isMe = it.memberId == memberId,
                        member = memberCacheProvider.getMember(it.memberId),
                    )
                })
            }
        }
    }

}