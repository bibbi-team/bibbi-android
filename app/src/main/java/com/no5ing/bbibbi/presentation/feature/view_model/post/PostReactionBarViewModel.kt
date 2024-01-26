package com.no5ing.bbibbi.presentation.feature.view_model.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.MemberCacheProvider
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.uistate.post.NormalPostReactionUiState
import com.no5ing.bbibbi.presentation.feature.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.feature.uistate.post.RealEmojiPostReactionUiState
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
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
                NormalPostReactionUiState(
                    reactionId = "temp",
                    memberId = memberId,
                    emojiType = emoji,
                    isMe = true,
                    member = memberCacheProvider.getMember(memberId),
                )
            )
            setState(previousList)
        }
    }

    fun reactRealEmoji(
        memberId: String,
        realEmojiType: String,
        realEmojiUrl: String,
        realEmojiId: String
    ) {
        val previousList = uiState.value.toMutableList()
        viewModelScope.launch {
            previousList.add(
                RealEmojiPostReactionUiState(
                    reactionId = "temp",
                    memberId = memberId,
                    emojiType = realEmojiType,
                    isMe = true,
                    member = memberCacheProvider.getMember(memberId),
                    imageUrl = realEmojiUrl,
                    realEmojiId = realEmojiId
                )
            )
            setState(previousList)
        }
    }

    private fun unReactEmoji(emoji: String) {
        val previousList = uiState.value.toMutableList()
        previousList.removeIf {
            it.emojiType == emoji && it.isMe
        }
        setState(previousList)
    }

    private fun unReactRealEmoji(realEmojiId: String) {
        val previousList = uiState.value.toMutableList()
        previousList.removeIf {
            it is RealEmojiPostReactionUiState && it.realEmojiId == realEmojiId && it.isMe
        }
        setState(previousList)
    }

    fun toggleEmoji(memberId: String, emojiType: String): Boolean {
        val isMeReacted = uiState.value.any {
            it.emojiType == emojiType && it.isMe
        }
        return if (isMeReacted) {
            unReactEmoji(emojiType)
            false
        } else {
            reactMe(memberId, emojiType)
            true
        }
    }

    fun hasRealEmoji(memberId: String, realEmojiId: String): Boolean {
        return uiState.value.any {
            it is RealEmojiPostReactionUiState && it.realEmojiId == realEmojiId && it.memberId == memberId
        }
    }

    fun hasEmoji(memberId: String, emojiType: String): Boolean {
        return uiState.value.any {
            it is NormalPostReactionUiState && it.emojiType == emojiType && it.memberId == memberId
        }
    }


    fun toggleRealEmoji(memberId: String, realEmoji: MemberRealEmoji): Boolean {
        return toggleRealEmoji(memberId, realEmoji.realEmojiId, realEmoji.imageUrl, realEmoji.type)
    }

    fun toggleRealEmoji(
        memberId: String,
        realEmojiId: String,
        realEmojiUrl: String,
        realEmojiType: String
    ): Boolean {
        val isMeReacted = uiState.value.any {
            it is RealEmojiPostReactionUiState && it.realEmojiId == realEmojiId && it.isMe
        }
        return if (isMeReacted) {
            unReactRealEmoji(realEmojiId)
            false
        } else {
            reactRealEmoji(
                memberId = memberId,
                realEmojiType = realEmojiType,
                realEmojiId = realEmojiId,
                realEmojiUrl = realEmojiUrl
            )
            true
        }
    }


    override fun invoke(arguments: Arguments) {
        val postId = arguments.get("postId") ?: throw RuntimeException()
        val memberId = arguments.get("memberId") ?: throw RuntimeException()
        setState(initState())
        withMutexScope(Dispatchers.IO) {
            val postReactions = async {
                restAPI
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
                        NormalPostReactionUiState(
                            reactionId = it.reactionId,
                            memberId = it.memberId,
                            emojiType = it.emojiType,
                            isMe = it.memberId == memberId,
                            member = memberCacheProvider.getMember(it.memberId),
                        )
                    }
                    val second = realEmojisData.results.parallelMap {
                        RealEmojiPostReactionUiState(
                            reactionId = it.postRealEmojiId,
                            memberId = it.memberId,
                            imageUrl = it.emojiImageUrl,
                            isMe = it.memberId == memberId,
                            member = memberCacheProvider.getMember(it.memberId),
                            emojiType = it.emojiType,
                            realEmojiId = it.realEmojiId
                        )
                    }
                    val resultList = (first + second).sortedBy { it.reactionId }
                    setState(resultList)
                }

            }
        }
    }

}