package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.main.home.HomePageContentState
import com.no5ing.bbibbi.presentation.state.main.home.HomePageStoryBarState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageContentState
import com.no5ing.bbibbi.presentation.state.main.home.rememberHomePageStoryBarState
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMembersViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.MainPostFeedViewModel
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.gapBetweenNow
import com.no5ing.bbibbi.util.todayAsString

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomePageContent(
    familyPostsViewModel: MainPostFeedViewModel = hiltViewModel(),
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    homePageContentState: HomePageContentState = rememberHomePageContentState(
        uiState = familyPostsViewModel.uiState
    ),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    storyBarState: HomePageStoryBarState = rememberHomePageStoryBarState(
        uiState = familyMembersViewModel.uiState
    ),
    onTapContent: (Post) -> Unit = {},
    onTapProfile: (Member) -> Unit = {},
    onTapInvite: () -> Unit = {},
) {
    val postItems = homePageContentState.uiState.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        if (familyPostsViewModel.isInitialize()) {
            familyPostsViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "date" to todayAsString(),
                    )
                )
            )
        } else {
            postItems.refresh()
        }
    }
    val pullRefreshStyle = rememberPullRefreshState(
        refreshing = postItems.loadState.refresh is LoadState.Loading,
        onRefresh = {
            familyMembersViewModel.invoke(Arguments())
            retrieveMeViewModel.invoke(Arguments())
            postItems.refresh()
        }
    )
    Box(modifier = Modifier.pullRefresh(pullRefreshStyle)) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(count = 2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                HomePageStoryBar(
                    familyMembersViewModel = familyMembersViewModel,
                    retrieveMeViewModel = retrieveMeViewModel,
                    storyBarState = storyBarState,
                    onTapProfile = onTapProfile,
                    onTapInvite = onTapInvite,
                )
            }
            item(span = { GridItemSpan(2) }) {
                Divider(thickness = 1.dp, color = MaterialTheme.bbibbiScheme.backgroundSecondary)
            }
            item(span = { GridItemSpan(2) }) {
                UploadCountDownBar()
            }
            // Timber.d("Current item cnt : ${postItems.itemCount}")
            if (postItems.itemCount > 0)
                items(
                    count = postItems.itemCount,
                    key = {
                        postItems[it]!!.post.postId
                    }
                ) {
                    val item = postItems[it] ?: throw RuntimeException()
                    HomePageContentItem(
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(300),
                        ),
                        imageUrl = item.post.imageUrl,
                        writerName = item.writer.name,
                        time = gapBetweenNow(time = item.post.createdAt),
                        onTap = { onTapContent(item.post) }
                    )
                }
            else
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(400.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ppippi),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(171.dp),
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
fun HomePageContentItem(
    modifier: Modifier,
    imageUrl: String,
    writerName: String,
    time: String,
    onTap: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = asyncImagePainter(source = imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .clip(RoundedCornerShape(24.dp))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp)
        ) {
            Text(
                text = writerName,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                color = MaterialTheme.bbibbiScheme.textPrimary,
                modifier = Modifier.widthIn(max = 110.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = time,
                style = MaterialTheme.bbibbiTypo.caption,
                color = MaterialTheme.bbibbiScheme.icon,
            )
        }
    }
}