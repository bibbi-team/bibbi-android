package com.no5ing.bbibbi.presentation.feature.view.main.family_studio

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.AIPost
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.gapBetweenNow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyStudioPageFeed(
    postItemsState: StateFlow<PagingData<AIPost>>,
    onTapContent: (AIPost) -> Unit = {},
    onPullToRefresh: () -> Unit = {},
) {
    val postItems = postItemsState.collectAsLazyPagingItems()
    val pullRefreshStyle = rememberPullRefreshState(
        refreshing = postItems.loadState.refresh is LoadState.Loading,
        onRefresh = {
            onPullToRefresh()
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
                    painter = painterResource(R.drawable.bbibbi),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(height = 182.dp, width = 335.dp),
                    contentScale = ContentScale.FillWidth,
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
                    FamilyStudioContentItem(
                        imageUrl = item.imageUrl,
                        time = gapBetweenNow(time = item.createdAt),
                        onTap = { onTapContent(item) },
                        authorName = item.authorName ?: "알 수 없음"
                    )
                }
                item {
                    Box(modifier = Modifier.height(36.dp))
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
fun FamilyStudioContentItem(
    imageUrl: String,
    authorName: String,
    time: String,
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = authorName,
                color = MaterialTheme.bbibbiScheme.textPrimary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
            Text(
                text = time,
                color = MaterialTheme.bbibbiScheme.icon,
                style = MaterialTheme.bbibbiTypo.caption,
            )
        }
    }
}