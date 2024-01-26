package com.no5ing.bbibbi.presentation.view.post.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.view.dialog.PostCommentDialog
import com.no5ing.bbibbi.presentation.view.dialog.ReactionListDialog
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.uistate.post.RealEmojiPostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.AddRealEmojiViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemoveRealEmojiViewModel
import com.no5ing.bbibbi.util.LocalSessionState

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
    onTapAddEmojiButton: () -> Unit,
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val memberId = LocalSessionState.current.memberId
    val uiState = familyPostReactionBarViewModel.uiState.collectAsState()
    val selectedEmoji = remember { mutableStateOf<PostReactionUiState>(PostReactionUiState.mock()) }
    val emojiMap = uiState.value.groupBy { it.getGroupKey() }
    val groupEmoji = emojiMap.toList()
    val reactionDialogState = remember { mutableStateOf(false) }
    ReactionListDialog(
        isEnabled = reactionDialogState,
        myGroup = emojiMap[selectedEmoji.value.getGroupKey()] ?: emptyList(),
        selectedEmoji = selectedEmoji.value,
    )
    PostCommentDialog(
        postId = post.postId,
        isEnabled = postCommentDialogState,
    )
    Box(modifier = Modifier) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier//.fillMaxWidth()
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    PostCommentBoxIcon(
                        commentCount = post.commentCount,
                        onClick = {
                            postCommentDialogState.value = true
                        }
                    )
                    Box(
                        modifier = Modifier
                            .let {
                                if (isEmojiBarActive)
                                    it
                                        .background(
                                            color = MaterialTheme.bbibbiScheme.mainYellow.copy(alpha = 0.1f),
                                            RoundedCornerShape(100.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.bbibbiScheme.mainYellow,
                                            RoundedCornerShape(100.dp)
                                        )
                                else it
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(color = MaterialTheme.bbibbiScheme.button)
                            }
                            .size(width = 53.dp, height = 36.dp)
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
                    repeat(groupEmoji.size) {
                        val item = groupEmoji[it]
                        val isMeReacted = item.second.any { elem -> elem.isMe }
                        val emojiMetadata = item.second.first()
                        if (emojiMetadata is RealEmojiPostReactionUiState) {
                            PostViewRealEmojiElement(
                                emojiType = emojiMetadata.emojiType,
                                iconUrl = emojiMetadata.imageUrl,
                                emojiCnt = item.second.size,
                                isMeReacted = isMeReacted,
                                onTap = {
                                    val hasMatch = familyPostReactionBarViewModel.toggleRealEmoji(
                                        memberId = memberId,
                                        realEmojiId = emojiMetadata.realEmojiId,
                                        realEmojiUrl = emojiMetadata.imageUrl,
                                        realEmojiType = emojiMetadata.emojiType,
                                    )
                                    if (!hasMatch) {
                                        removeRealEmojiViewModel.invoke(
                                            Arguments(
                                                resourceId = post.postId,
                                                mapOf(
                                                    "realEmojiId" to emojiMetadata.realEmojiId
                                                )
                                            )
                                        )
                                    } else {
                                        addRealEmojiViewModel.invoke(
                                            Arguments(
                                                resourceId = post.postId,
                                                mapOf(
                                                    "realEmojiId" to emojiMetadata.realEmojiId
                                                )
                                            )
                                        )
                                    }

                                },
                                onLongTap = {
                                    selectedEmoji.value = emojiMetadata
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
                                    familyPostReactionBarViewModel.toggleEmoji(
                                        memberId = memberId,
                                        emojiType = item.first
                                    )
                                },
                                onLongTap = {
                                    selectedEmoji.value = item.second.first()
                                    reactionDialogState.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
