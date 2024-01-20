package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.parallelMap
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
                    member = memberCacheProvider.getMember(memberId),
                    isRealEmoji = false,
                    realEmojiUrl = null,
                )
            )
            setState(previousList)
        }
    }

    private fun reactRealEmoji(memberId: String, emoji: MemberRealEmoji) {
        val previousList = uiState.value.toMutableList()
        viewModelScope.launch {
            previousList.add(
                PostReactionUiState(
                    reactionId = "temp",
                    memberId = memberId,
                    emojiType = emoji.realEmojiId,
                    isMe = true,
                    member = memberCacheProvider.getMember(memberId),
                    isRealEmoji = true,
                    realEmojiUrl = emoji.imageUrl,
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

    fun toggleReact(memberId: String, realEmoji: MemberRealEmoji): Boolean {
        val isMeReacted = uiState.value.any {
            it.emojiType == realEmoji.realEmojiId && it.isMe
        }
        if (isMeReacted) {
            unReactMe(realEmoji.realEmojiId)
            return false
        } else {
            reactRealEmoji(memberId, realEmoji)
            return true
        }
    }

    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        setState(initState())
        withMutexScope(Dispatchers.IO) {
            val postReactions = async { restAPI
                    .getPostApi()
                .getPostReactions(
                    postId = postId,
                )
            }
            val realEmojiReactions = async {
                restAPI.getPostApi().getPostRealEmojiList(
                    postId = postId,
                )
            }
            postReactions.await().suspendOnSuccess {
                val postReactionsData = data
                realEmojiReactions.await().suspendOnSuccess {
                    val realEmojisData = data
                    val first = postReactionsData.results.parallelMap {
                        PostReactionUiState(
                            reactionId = it.reactionId,
                            memberId = it.memberId,
                            emojiType = it.emojiType,
                            isMe = it.memberId == memberId,
                            member = memberCacheProvider.getMember(it.memberId),
                            isRealEmoji = false,
                            realEmojiUrl = null,
                        )
                    }
                    val second = realEmojisData.results.parallelMap {
                        PostReactionUiState(
                            reactionId = it.postRealEmojiId,
                            memberId = it.memberId,
                            realEmojiUrl = it.emojiImageUrl,
                            isMe = it.memberId == memberId,
                            member = memberCacheProvider.getMember(it.memberId),
                            isRealEmoji = true,
                            emojiType = it.realEmojiId
                        )
                    }
                    val resultList = (first + second).sortedBy { it.reactionId }
                    setState(resultList)
                }

            }
        }
    }

}