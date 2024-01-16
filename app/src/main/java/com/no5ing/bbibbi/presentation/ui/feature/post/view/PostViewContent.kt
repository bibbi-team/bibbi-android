package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.AddReactionBar
import com.no5ing.bbibbi.presentation.ui.common.component.MiniTextBubbleBox
import com.no5ing.bbibbi.presentation.ui.feature.dialog.PostCommentDialog
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun PostViewContent(
    post: Post,
    modifier: Modifier = Modifier,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    addEmojiBarState: MutableState<Boolean> = remember { mutableStateOf(false) },
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val coroutineScope = rememberCoroutineScope()
    val memberId = LocalSessionState.current.memberId
    PostCommentDialog(
        postId = post.postId,
        isEnabled = postCommentDialogState,
        coroutineScope = coroutineScope
    )
    Column(
        modifier = modifier,
    ) {
        Box {
            Box {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            MiniTextBubbleBox(
                text = post.content,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                PostViewReactionBar(
                    modifier = Modifier.weight(1.0f),
                    postId = post.postId,
                    onTapAddEmojiButton = {
                        addEmojiBarState.value = !addEmojiBarState.value
                    },
                    familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                    removePostReactionViewModel = removePostReactionViewModel,
                    addPostReactionViewModel = addPostReactionViewModel,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = MaterialTheme.bbibbiScheme.backgroundSecondary)
                        .padding(vertical = 5.dp, horizontal = 6.dp)
                        .clickable { postCommentDialogState.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.message_icon),
                            contentDescription = null,
                            tint = MaterialTheme.bbibbiScheme.textPrimary,
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.comment_count, post.commentCount),
                            color = MaterialTheme.bbibbiScheme.textPrimary,
                            style = MaterialTheme.bbibbiTypo.bodyOneBold,
                        )
                    }

                }
            }

            if (addEmojiBarState.value) {
                AddReactionBar(
                    onTapEmoji = {
                        val toggled =
                            familyPostReactionBarViewModel.toggleReact(
                                memberId = memberId,
                                emoji = it
                            )

                        if (toggled) {
                            addPostReactionViewModel.invoke(
                                Arguments(
                                    resourceId = post.postId,
                                    mapOf(
                                        "emoji" to it
                                    )
                                )
                            )
                        } else {
                            removePostReactionViewModel.invoke(
                                Arguments(
                                    resourceId = post.postId,
                                    mapOf(
                                        "emoji" to it
                                    )
                                )
                            )
                        }
                        addEmojiBarState.value = false
                    },
                    onDispose = {
                        addEmojiBarState.value = false
                    }
                )
            }
        }

    }
}