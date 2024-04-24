package com.no5ing.bbibbi.presentation.feature.view.main.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.presentation.component.MicroTextBubbleBox
import com.no5ing.bbibbi.presentation.feature.view.common.PostTypeSwitchButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.toLocalizedDate
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilePageContent(
    onTapContent: (Post) -> Unit = {},
    postItemsState: StateFlow<PagingData<Post>>,
    missionItemState: StateFlow<PagingData<Post>>,
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) }
) {
    val pagerState = rememberPagerState { 2 }
    LaunchedEffect(pagerState.currentPage) {
        val type = if (pagerState.currentPage == 0) PostType.SURVIVAL else PostType.MISSION
        if (type != postViewTypeState.value) {
            postViewTypeState.value = type
        }
    }
    LaunchedEffect(postViewTypeState.value) {
        val page = if (postViewTypeState.value == PostType.SURVIVAL) 0 else 1
        pagerState.animateScrollToPage(page)
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PostTypeSwitchButton(
                isLocked = false,
                state = postViewTypeState
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) {
            when(it) {
                0 -> {
                    SurvivalProfilePageFeed(
                        postItemsState = postItemsState,
                        onTapContent = onTapContent
                    )
                }
                1 -> {
                    MissionProfilePageFeed(
                        postItemsState = missionItemState,
                        onTapContent = onTapContent
                    )
                }
            }
            
        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SurvivalProfilePageFeed(
    postItemsState: StateFlow<PagingData<Post>>,
    onTapContent: (Post) -> Unit = {},
) {
    val postItems = postItemsState.collectAsLazyPagingItems()
    val pullRefreshStyle = rememberPullRefreshState(
        refreshing = postItems.loadState.refresh is LoadState.Loading,
        onRefresh = {
            postItems.refresh()
        }
    )
    Box(modifier = Modifier.pullRefresh(pullRefreshStyle)) {
        if (postItems.itemCount == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.no_uploaded_image),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(width = 126.dp, height = 118.dp),
                    contentScale = ContentScale.FillWidth,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.profile_image_not_exists),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )

            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(count = 2),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(postItems.itemCount) {
                    val item = postItems[it] ?: throw RuntimeException()
                    ProfilePageContentItem(
                        imageUrl = item.imageUrl,
                        emojiCnt = item.emojiCount,
                        commentCnt = item.commentCount,
                        time = toLocalizedDate(time = item.createdAt),
                        onTap = { onTapContent(item) },
                        postContent = item.content,
                        isMission = false,
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = postItems.loadState.refresh is LoadState.Loading,
            state = pullRefreshStyle,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
            contentColor = MaterialTheme.bbibbiScheme.iconSelected,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MissionProfilePageFeed(
    postItemsState: StateFlow<PagingData<Post>>,
    onTapContent: (Post) -> Unit = {},
) {
    val postItems = postItemsState.collectAsLazyPagingItems()
    val pullRefreshStyle = rememberPullRefreshState(
        refreshing = postItems.loadState.refresh is LoadState.Loading,
        onRefresh = {
            postItems.refresh()
        }
    )
    Box(modifier = Modifier.pullRefresh(pullRefreshStyle)) {
        if (postItems.itemCount == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.no_uploaded_image),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(width = 126.dp, height = 118.dp),
                    contentScale = ContentScale.FillWidth,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.profile_image_not_exists),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )

            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(count = 2),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(postItems.itemCount) {
                    val item = postItems[it] ?: throw RuntimeException()
                    ProfilePageContentItem(
                        imageUrl = item.imageUrl,
                        emojiCnt = item.emojiCount,
                        commentCnt = item.commentCount,
                        time = toLocalizedDate(time = item.createdAt),
                        onTap = { onTapContent(item) },
                        postContent = item.content,
                        isMission = true,
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = postItems.loadState.refresh is LoadState.Loading,
            state = pullRefreshStyle,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
            contentColor = MaterialTheme.bbibbiScheme.iconSelected,
        )
    }
}
@Composable
fun ProfilePageContentItem(
    imageUrl: String,
    postContent: String,
    emojiCnt: Int,
    commentCnt: Int,
    time: String,
    isMission: Boolean,
    onTap: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalAlignment = Alignment.Start,
    ) {
        Box {
            AsyncImage(
                model = asyncImagePainter(source = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0f)
                    .clip(RoundedCornerShape(24.dp))
            )
            MicroTextBubbleBox(
                text = postContent,
                alignment = Alignment.BottomCenter,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            if (isMission) {
                Box(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mission_diamond),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.emoji_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.bbibbiScheme.icon
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = emojiCnt.toString(),
                    color = MaterialTheme.bbibbiScheme.textPrimary,
                    style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.chat_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.bbibbiScheme.icon
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = commentCnt.toString(),
                    color = MaterialTheme.bbibbiScheme.textPrimary,
                    style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                color = MaterialTheme.bbibbiScheme.icon,
                style = MaterialTheme.bbibbiTypo.caption,
            )
        }
    }
}