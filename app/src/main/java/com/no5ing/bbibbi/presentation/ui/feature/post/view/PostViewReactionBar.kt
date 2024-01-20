 package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.feature.dialog.PostCommentDialog
import com.no5ing.bbibbi.presentation.ui.feature.dialog.ReactionListDialog
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.AddRealEmojiViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemoveRealEmojiViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getEmojiResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostViewReactionBar(
    modifier: Modifier,
    post: Post,
    isEmojiBarActive: Boolean,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    addRealEmojiViewModel: AddRealEmojiViewModel = hiltViewModel(),
    removeRealEmojiViewModel: RemoveRealEmojiViewModel = hiltViewModel(),
    //   postViewReactionMemberViewModel: PostViewReactionMemberViewModel = hiltViewModel(),
    onTapAddEmojiButton: () -> Unit,
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val memberId = LocalSessionState.current.memberId
    val uiState = familyPostReactionBarViewModel.uiState.collectAsState()
    LaunchedEffect(post) {
        familyPostReactionBarViewModel.invoke(
            Arguments(
                arguments = mapOf(
                    "postId" to post.postId,
                    "memberId" to memberId
                )
            )
        )
    }
    val selectedEmoji = remember { mutableStateOf(emojiList.first()) }
    val selectedEmojiKey = remember { mutableStateOf(emojiList.first()) }
    val emojiMap =  uiState.value.groupBy { it.emojiType }
    val groupEmoji =  emojiMap.toList()
    val reactionDialogState = remember { mutableStateOf(false) }
    ReactionListDialog(
        isEnabled = reactionDialogState,
        myGroup = emojiMap[selectedEmojiKey.value] ?: emptyList(),
        selectedEmoji = selectedEmoji.value,
    )
    PostCommentDialog(
        postId = post.postId,
        isEnabled = postCommentDialogState,
    )
    Box(modifier = Modifier) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier//.fillMaxWidth()
        ) {
            repeat(groupEmoji.size) {
                val item = groupEmoji[it]
                val isMeReacted = item.second.any { elem -> elem.isMe }
                val isRealEmoji = item.second.first().isRealEmoji
                if(isRealEmoji) {
                    PostViewRealEmojiElement(
                        iconUrl = item.second.first().realEmojiUrl!!,
                        emojiCnt = item.second.size,
                        isMeReacted = isMeReacted,
                        onTap = {
                            if (isMeReacted) {
                                removeRealEmojiViewModel.invoke(
                                    Arguments(
                                        resourceId = post.postId,
                                        mapOf(
                                            "realEmojiId" to item.first
                                        )
                                    )
                                )
                            } else {
                                addRealEmojiViewModel.invoke(
                                    Arguments(
                                        resourceId = post.postId,
                                        mapOf(
                                            "realEmojiId" to item.first
                                        )
                                    )
                                )
                            }
                            familyPostReactionBarViewModel.toggleReact(
                                memberId = memberId,
                                emoji = item.first
                            )
                        },
                        onLongTap = {
                            selectedEmojiKey.value = item.first
                            selectedEmoji.value = item.second.first().realEmojiUrl!!
                            reactionDialogState.value = true
                        }
                    )
                } else {
                    PostViewReactionElement(
                        emojiType = item.first,
                        emojiCnt = item.second.size,
                        isMeReacted = isMeReacted,
                        onTap = {
                            if (isMeReacted) {
                                removePostReactionViewModel.invoke(
                                    Arguments(
                                        resourceId = post.postId,
                                        mapOf(
                                            "emoji" to item.first
                                        )
                                    )
                                )
                            } else {
                                addPostReactionViewModel.invoke(
                                    Arguments(
                                        resourceId = post.postId,
                                        mapOf(
                                            "emoji" to item.first
                                        )
                                    )
                                )
                            }
                            familyPostReactionBarViewModel.toggleReact(
                                memberId = memberId,
                                emoji = item.first
                            )
                        },
                        onLongTap = {
                            selectedEmojiKey.value = item.first
                            selectedEmoji.value = item.first
                            reactionDialogState.value = true
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .let {
                        if(isEmojiBarActive)
                            it
                                .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
                                .border(
                                1.dp, MaterialTheme.bbibbiScheme.mainGreen, RoundedCornerShape(100.dp)
                            )
                                .padding(vertical = 6.dp, horizontal = 8.dp)
                        else it.clip(RoundedCornerShape(100.dp))
                            .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    }
                    .clickable { onTapAddEmojiButton() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_emoji_icon),
                    contentDescription = null,
                    tint = MaterialTheme.bbibbiScheme.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            PostCommentBoxIcon(
                commentCount = post.commentCount,
                onClick = {
                    postCommentDialogState.value = true
                }
            )
        }
    }
}
