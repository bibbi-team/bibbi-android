package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.AddReactionBar
import com.no5ing.bbibbi.presentation.ui.common.component.MiniTextBubbleBox
import com.no5ing.bbibbi.presentation.ui.common.component.OuterClickListener
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import timber.log.Timber

@Composable
fun PostViewContent(
    post: Post,
    modifier: Modifier = Modifier,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    addEmojiBarState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
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
            PostViewReactionBar(
                postId = post.postId,
                onTapAddEmojiButton = {
                    addEmojiBarState.value = !addEmojiBarState.value
                },
                familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                removePostReactionViewModel = removePostReactionViewModel,
                addPostReactionViewModel = addPostReactionViewModel,
            )
            if (addEmojiBarState.value) {
                AddReactionBar(
                    onTapEmoji = {
                        val toggled =
                            familyPostReactionBarViewModel.toggleReact(emoji = it)

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