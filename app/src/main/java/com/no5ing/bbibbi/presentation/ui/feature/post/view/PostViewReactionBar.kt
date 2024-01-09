package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.feature.dialog.ReactionListDialog
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.members.PostViewReactionMemberViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getEmojiResource

@Composable
fun PostViewReactionBar(
    postId: String,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    postViewReactionMemberViewModel: PostViewReactionMemberViewModel = hiltViewModel(),
    uiState: State<List<PostReactionUiState>> = familyPostReactionBarViewModel.uiState.collectAsState(),
    onTapAddEmojiButton: () -> Unit,
) {

    LaunchedEffect(postId) {
        familyPostReactionBarViewModel.invoke(Arguments(arguments = mapOf("postId" to postId)))
        postViewReactionMemberViewModel.invoke(Arguments())
    }
    val selectedEmoji = remember { mutableStateOf(emojiList.first()) }
    val emojiMap = uiState.value.groupBy { it.emojiType }
    val groupEmoji = emojiMap.toList()
    val reactionDialogState = remember { mutableStateOf(false) }
    ReactionListDialog(
        isEnabled = reactionDialogState,
        emojiMap = emojiMap,
        selectedEmoji = selectedEmoji.value,
        postViewReactionMemberViewModel = postViewReactionMemberViewModel,
    )
    Box(modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(groupEmoji.size) {
                val item = groupEmoji[it]
                val isMeReacted = item.second.any { elem -> elem.isMe }
                PostViewReactionElement(
                    emojiType = item.first,
                    emojiCnt = item.second.size,
                    isMeReacted = isMeReacted,
                    onTap = {
                        if (isMeReacted) {
                            removePostReactionViewModel.invoke(
                                Arguments(
                                    resourceId = postId,
                                    mapOf(
                                        "emoji" to item.first
                                    )
                                )
                            )
                        } else {
                            addPostReactionViewModel.invoke(
                                Arguments(
                                    resourceId = postId,
                                    mapOf(
                                        "emoji" to item.first
                                    )
                                )
                            )
                        }
                        familyPostReactionBarViewModel.toggleReact(emoji = item.first)
                    },
                    onLongTap = {
                        selectedEmoji.value = item.first
                        reactionDialogState.value = true
                    }
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                        .clickable { onTapAddEmojiButton() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_emoji_icon),
                        contentDescription = null,
                        tint = MaterialTheme.bbibbiScheme.textPrimary,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostViewReactionElement(
    emojiType: String,
    emojiCnt: Int,
    isMeReacted: Boolean,
    onTap: () -> Unit,
    onLongTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .border(
                width = if (isMeReacted) 1.dp else 0.dp,
                color = MaterialTheme.bbibbiScheme.iconSelected,
                RoundedCornerShape(100.dp)
            )
            .background(
                color = if (isMeReacted) MaterialTheme.bbibbiScheme.button else MaterialTheme.bbibbiScheme.backgroundPrimary,
                RoundedCornerShape(100.dp)
            )
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongTap,
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = getEmojiResource(emojiName = emojiType),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = emojiCnt.toString(),
                color = if (isMeReacted) MaterialTheme.bbibbiScheme.iconSelected
                else MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyOneBold,
            )
        }

    }
}