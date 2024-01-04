package com.no5ing.bbibbi.presentation.ui.feature.post.view

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.feature.dialog.ReactionListDialog
import com.no5ing.bbibbi.presentation.uistate.post.PostReactionUiState
import com.no5ing.bbibbi.presentation.viewmodel.members.PostViewReactionMemberViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.CustomDialogPosition
import com.no5ing.bbibbi.util.customDialogModifier
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
                        .background(color = MaterialTheme.colorScheme.onBackground)
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                        .clickable { onTapAddEmojiButton() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_emoji_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
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
                color = MaterialTheme.colorScheme.onSurface,
                RoundedCornerShape(100.dp)
            )
            .background(
                color = if (isMeReacted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
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
                fontSize = 16.sp,
                color = if (isMeReacted) Color.White else MaterialTheme.colorScheme.tertiary,
            )
        }

    }
}