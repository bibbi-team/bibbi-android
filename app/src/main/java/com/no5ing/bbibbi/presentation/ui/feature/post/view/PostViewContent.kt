package com.no5ing.bbibbi.presentation.ui.feature.post.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.no5ing.bbibbi.presentation.viewmodel.post.AddRealEmojiViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.MemberRealEmojiListViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemoveRealEmojiViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.asyncImagePainter

@Composable
fun PostViewContent(
    post: Post,
    modifier: Modifier = Modifier,
    onTapRealEmojiCreate: (String) -> Unit,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    addRealEmojiViewModel: AddRealEmojiViewModel = hiltViewModel(),
    removeRealEmojiViewModel: RemoveRealEmojiViewModel = hiltViewModel(),
    postRealEmojiListViewModel: MemberRealEmojiListViewModel = hiltViewModel(),
    addEmojiBarState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val memberId = LocalSessionState.current.memberId
    val memberRealEmojiState by postRealEmojiListViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        postRealEmojiListViewModel.invoke(Arguments())
    }
    Column(
        modifier = modifier,
    ) {
        Box {
            Box {
                AsyncImage(
                    model = asyncImagePainter(source = post.imageUrl),
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
                    post = post,
                    isEmojiBarActive = addEmojiBarState.value,
                    onTapAddEmojiButton = {
                        addEmojiBarState.value = !addEmojiBarState.value
                    },
                    familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                    removePostReactionViewModel = removePostReactionViewModel,
                    addPostReactionViewModel = addPostReactionViewModel,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            if (addEmojiBarState.value) {
                Spacer(modifier = Modifier.height(8.dp))
                AddReactionBar(
                    modifier = Modifier.fillMaxWidth(),
                    realEmojiMap = memberRealEmojiState,
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
                            addEmojiBarState.value = false
                        }
                    },
                    onTapRealEmoji = {
                        val toggled =
                            familyPostReactionBarViewModel.toggleReact(
                                memberId = memberId,
                                realEmoji = it
                            )

                        if (toggled) {
                            addRealEmojiViewModel.invoke(
                                Arguments(
                                    resourceId = post.postId,
                                    mapOf(
                                        "realEmojiId" to it.realEmojiId
                                    )
                                )
                            )
                            addEmojiBarState.value = false
                        }
                    },
                    onDispose = {
                        addEmojiBarState.value = false
                    },
                    onTapRealEmojiCreate = onTapRealEmojiCreate,
                )
            }
        }

    }
}