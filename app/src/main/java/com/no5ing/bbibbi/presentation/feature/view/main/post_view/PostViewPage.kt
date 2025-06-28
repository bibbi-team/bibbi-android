package com.no5ing.bbibbi.presentation.feature.view.main.post_view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BannerAd
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.feature.state.post.view.PostViewPageState
import com.no5ing.bbibbi.presentation.feature.state.post.view.rememberPostViewPageState
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view_model.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.FamilyPostViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.FamilySwipePostsViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.getAdView
import com.no5ing.bbibbi.util.toLocalizedDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostViewPage(
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    postId: String,
    familyPostViewModel: FamilyPostViewModel = hiltViewModel(),
    postViewPageState: PostViewPageState = rememberPostViewPageState(
        uiState = familyPostViewModel.uiState
    ),
    familyPostsViewModel: FamilySwipePostsViewModel = hiltViewModel(),
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    LaunchedEffect(Unit) {
        familyPostViewModel.invoke(Arguments(resourceId = postId))
    }
    val memberId = LocalSessionState.current.memberId

    var isPagerReady by remember { mutableStateOf(false) }
    val postState by postViewPageState.uiState.collectAsState()
    val siblingPostState by familyPostsViewModel.uiState.collectAsState()
    val pagerState = key(siblingPostState) {
        rememberPagerState(
            initialPage = if (siblingPostState.isReady()) siblingPostState.data
                .indexOfFirst { it.post.postId == postId } else 0,
            pageCount = {
                if (siblingPostState.isReady()) siblingPostState.data.size else 1
            }
        )
    }
    val adView = getAdView()
    LaunchedEffect(postState) {
        if (postState.isReady()) {
            val currentPost = postState.data.post
            familyPostsViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "date" to currentPost.createdAt.toLocalDate().toString(),
                    )
                )
            )
        }
    }
    LaunchedEffect(postState, postCommentDialogState.value) {
        if (!postCommentDialogState.value && postState.isReady()) {
            val currentPost = postState.data.post
            familyPostsViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "date" to currentPost.createdAt.toLocalDate().toString(),
                    )
                )
            )
        }
    }
    LaunchedEffect(siblingPostState) {
        if (siblingPostState.isReady()) {
            isPagerReady = true
        }
    }
    LaunchedEffect(postState, pagerState.currentPage) {
        if (postState.isReady()) {
            val currentPostId =
                (if (siblingPostState.isReady()) siblingPostState.data.getOrNull(pagerState.currentPage)?.post?.postId
                else postState.data.post.postId)
                    ?: return@LaunchedEffect
            familyPostReactionBarViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "postId" to currentPostId,
                        "memberId" to memberId
                    )
                )
            )
        }
    }
    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Box {
            AnimatedVisibility(
                postState.isReady(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = asyncImagePainter(
                            source =
                            if (postState.isReady())
                                if (siblingPostState.isReady()) siblingPostState.data.getOrNull(
                                    pagerState.currentPage
                                )?.post?.postImgUrl
                                else postState.data.post.imageUrl
                            else null
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(50.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.1f
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    if (postState.isReady()) {
                        if (isPagerReady) {
                            HorizontalPager(state = pagerState) { page ->
                                val postData =
                                    siblingPostState.data.getOrNull(page) ?: return@HorizontalPager
                                PostViewBody(
                                    onDispose = onDispose,
                                    onTapProfile = onTapProfile,
                                    onTapRealEmojiCreate = onTapRealEmojiCreate,
                                    familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                                    removePostReactionViewModel = removePostReactionViewModel,
                                    addPostReactionViewModel = addPostReactionViewModel,
                                    postData = MainFeedUiState(
                                        postData.post.toPost(),
                                        postData.writer
                                    ),
                                    postCommentDialogState = postCommentDialogState,
                                    missionText = postData.post.missionContent
                                )
                            }
                        } else {
                            PostViewBody(
                                onDispose = onDispose,
                                onTapProfile = onTapProfile,
                                onTapRealEmojiCreate = onTapRealEmojiCreate,
                                familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                                removePostReactionViewModel = removePostReactionViewModel,
                                addPostReactionViewModel = addPostReactionViewModel,
                                postData = postState.data,
                                postCommentDialogState = postCommentDialogState,
                                missionText = null
                            )
                        }

                    }
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars)) {
                BannerAd(adView = adView, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun PostViewBody(
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    postData: MainFeedUiState,
    missionText: String? = null,
    familyPostReactionBarViewModel: PostReactionBarViewModel,
    removePostReactionViewModel: RemovePostReactionViewModel,
    addPostReactionViewModel: AddPostReactionViewModel,
    postCommentDialogState: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    Column {
        PostViewTopBar(
            onTap = {
                onTapProfile(postData.writer)
            },
            onDispose = onDispose,
            member = postData.writer,
            date = toLocalizedDate(postData.post.createdAt)
        )
        Spacer(modifier = Modifier.height(48.dp))
        PostViewContent(
            post = postData.post,
            familyPostReactionBarViewModel = familyPostReactionBarViewModel,
            removePostReactionViewModel = removePostReactionViewModel,
            addPostReactionViewModel = addPostReactionViewModel,
            onTapRealEmojiCreate = onTapRealEmojiCreate,
            postCommentDialogState = postCommentDialogState,
            missionText = missionText,
        )
    }
}

@Composable
fun PostViewTopBar(
    onTap: () -> Unit,
    onDispose: () -> Unit,
    date: String,
    member: Member,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.return_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(52.dp)
                .clickable { onDispose() }
        )
        CircleProfileImage(
            member = member,
            size = 40.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = member.name,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
            )
            Text(
                text = date,
                color = MaterialTheme.bbibbiScheme.icon,
                fontSize = 12.sp,
            )

        }
    }
}