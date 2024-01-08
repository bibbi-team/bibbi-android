package com.no5ing.bbibbi.presentation.ui.feature.main.profile

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.post.FamilyPostsViewModel
import com.no5ing.bbibbi.util.toLocalizedDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfilePageContent(
    memberId: String,
    familyPostsViewModel: FamilyPostsViewModel = hiltViewModel(),
    onTapContent: (Post) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        familyPostsViewModel.invoke(Arguments(arguments = mapOf("memberId" to memberId)))
    }
    val postItems = familyPostsViewModel.uiState.collectAsLazyPagingItems()
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
                    painter = painterResource(R.drawable.ppippi),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(171.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.profile_image_not_exists),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                    ),
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
                        time = toLocalizedDate(time = item.createdAt),
                        onTap = { onTapContent(item) }
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = postItems.loadState.refresh is LoadState.Loading,
            state = pullRefreshStyle,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun ProfilePageContentItem(
    imageUrl: String,
    emojiCnt: Int,
    time: String,
    onTap: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalAlignment = Alignment.Start,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .error(R.drawable.ppippi)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(24.dp))
        )
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
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = emojiCnt.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = time,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}