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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.data.model.view.MainPageModel
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedStoryElementUiState
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view.common.PostTypeSwitchButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.gapBetweenNow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePageContent(
    mainPageState: StateFlow<APIResponse<MainPageModel>>,
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) },
    onTapContent: (String) -> Unit = {},
    onTapProfile: (String) -> Unit = {},
    onTapInvite: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val mainPageModel by mainPageState.collectAsState()
    val postItems = if(mainPageModel.isReady())
        if(postViewTypeState.value == PostType.MISSION) mainPageModel.data.missionFeeds
        else mainPageModel.data.survivalFeeds
    else emptyList()
    var isRefreshing by remember { mutableStateOf(true) }
    LaunchedEffect(mainPageModel) {
        if (mainPageModel.isReady()) {
            isRefreshing = false
        }
    }

    HomePageFeedGrid(
        isRefreshing = isRefreshing,
        onRefresh = {
            if (isRefreshing) return@HomePageFeedGrid
            isRefreshing = true
            onRefresh()
        }
    ) {
        item(
            key = "TopBar",
            span = { GridItemSpan(2) }) {
            Column {
                HomePageStoryBar(
                    mainPageState = mainPageState,
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
                    PostTypeSwitchButton(
                        isLocked = mainPageModel.isReady() && !mainPageModel.data.isMissionUnlocked,
                        state = postViewTypeState
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
        if (postItems.isNotEmpty()) {
            items(
                count = postItems.size,
                key = {
                    postItems[it].postId
                }
            ) {
                val item = postItems[it]
                HomePageFeedElement(
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(300),
                    ),
                    imageUrl = item.imageUrl,
                    writerName = item.authorName,
                    time = gapBetweenNow(time = item.createdAt),
                    onTap = { onTapContent(item.postId) },
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

