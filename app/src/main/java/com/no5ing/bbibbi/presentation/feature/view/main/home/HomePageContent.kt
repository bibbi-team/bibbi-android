package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedStoryElementUiState
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view.common.PostTypeSwitchButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.gapBetweenNow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePageContent(
    contentState: StateFlow<PagingData<MainFeedUiState>>,
    //familyListState: StateFlow<PagingData<Member>>,
    postTopState: StateFlow<APIResponse<List<MainFeedStoryElementUiState>>>,
    meState: StateFlow<APIResponse<Member>>,
    onTapContent: (Post) -> Unit = {},
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val postItems = contentState.collectAsLazyPagingItems()
    // val memberItems = familyListState.collectAsLazyPagingItems()
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(postItems.loadState.refresh) {
        if (isRefreshing &&
            postItems.loadState.refresh is LoadState.NotLoading
        ) {
            isRefreshing = false
        }
    }

    HomePageFeedGrid(
        isRefreshing = isRefreshing,
        onRefresh = {
            if (isRefreshing) return@HomePageFeedGrid
            isRefreshing = true
            postItems.refresh()
            onRefresh()
        }
    ) {
        item(
            key = "TopBar",
            span = { GridItemSpan(2) }) {
            Column {
                HomePageStoryBar(
                    postTopStateFlow = postTopState,
                    meStateFlow = meState,
                    onTapProfile = onTapProfile,
                    onTapInvite = onTapInvite,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.bbibbiScheme.backgroundSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                UploadCountDownBar()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PostTypeSwitchButton()
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
        if (postItems.itemCount > 0) {
            items(
                count = postItems.itemCount,
                key = {
                    postItems[it]!!.post.postId
                }
            ) {
                val item = postItems[it] ?: throw RuntimeException()
                HomePageFeedElement(
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(300),
                    ),
                    imageUrl = item.post.imageUrl,
                    writerName = item.writer.name,
                    time = gapBetweenNow(time = item.post.createdAt),
                    onTap = { onTapContent(item.post) },
                    postContent = item.post.content,
                )
            }
        } else {
            item(
                key = "Empty",
                span = { GridItemSpan(2) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.bbibbi),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                    )

                }
            }
        }
    }
}

