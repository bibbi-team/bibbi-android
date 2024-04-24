package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.data.model.view.MainPageFeedModel
import com.no5ing.bbibbi.data.model.view.MainPageModel
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.presentation.component.VerticalGrid
import com.no5ing.bbibbi.presentation.feature.view.common.PostTypeSwitchButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.gapBetweenNow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomePageContent(
    mainPageState: StateFlow<APIResponse<MainPageModel>>,
    deferredPickStateSet: StateFlow<Set<String>>,
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) },
    onTapContent: (String) -> Unit = {},
    onTapProfile: (String) -> Unit = {},
    onTapPick: (MainPageTopBarModel) -> Unit = {},
    onTapInvite: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val warningState = remember {
        mutableIntStateOf(0)
    }

    val mainPageModel by mainPageState.collectAsState()

    val survivalFeedItems = if (mainPageModel.isReady())
        mainPageModel.data.survivalFeeds
        else emptyList()
    val missionFeedItems = if (mainPageModel.isReady())
        mainPageModel.data.missionFeeds
    else emptyList()
    var isRefreshing by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val pullRefreshStyle = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            if (isRefreshing) return@rememberPullRefreshState
            isRefreshing = true
            onRefresh()
        }
    )
    LaunchedEffect(mainPageModel) {
        if (mainPageModel.isReady()) {
            isRefreshing = false
        }
    }
    LaunchedEffect(postViewTypeState.value) {
        val page = if (postViewTypeState.value == PostType.SURVIVAL) 0 else 1
        pagerState.animateScrollToPage(page)
    }
    LaunchedEffect(pagerState.currentPage) {
        val type = if (pagerState.currentPage == 0) PostType.SURVIVAL else PostType.MISSION
        if (type != postViewTypeState.value) {
            postViewTypeState.value = type
        }
    }
    BoxWithConstraints {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                // .fillMaxSize()
                .pullRefresh(pullRefreshStyle)
                .verticalScroll(state = scrollState)
        ) {
            HomePageStoryBar(
                items = if (mainPageModel.isReady()) mainPageModel.data.topBarElements else emptyList(),
                onTapProfile = onTapProfile,
                onTapInvite = onTapInvite,
                onTapPick = onTapPick,
                deferredPickStateSet = deferredPickStateSet,
            )
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.bbibbiScheme.backgroundSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            UploadCountDownBar(warningState = warningState)
            if(postViewTypeState.value == PostType.SURVIVAL) {
                SurvivalTextDescription(warningState = warningState)
            } else {
                MissionTextDescription(
                    warningState = warningState,
                    isMissionUnlocked = mainPageModel.isReady() && mainPageModel.data.isMissionUnlocked,
                    missionText = if(mainPageModel.isReady()) mainPageModel.data.dailyMissionContent else "",
                    remainingMemberCnt = if(mainPageModel.isReady()) mainPageModel.data.leftUploadCountUntilMissionUnlock else 0
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PostTypeSwitchButton(
                    isLocked = mainPageModel.isReady() && !mainPageModel.data.isMissionUnlocked,
                    state = postViewTypeState
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> SurvivalFeedTab(
                        postItems = survivalFeedItems,
                        onTapContent = onTapContent,
                    )

                    1 -> MissionFeedTab(
                        postItems = missionFeedItems,
                        isMissionUnlocked = mainPageModel.isReady() && mainPageModel.data.isMissionUnlocked,
                        onTapContent = onTapContent,
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshStyle,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.bbibbiScheme.backgroundSecondary,
            contentColor = MaterialTheme.bbibbiScheme.iconSelected,
        )
    }


}

@Composable
fun SurvivalFeedTab(
    postItems: List<MainPageFeedModel>,
    onTapContent: (String) -> Unit = {},
) {
    if (postItems.isEmpty()) {
        Column(
            modifier = Modifier
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
    } else {
        VerticalGrid {
            postItems.forEach { item ->
                HomePageFeedElement(
                    modifier = Modifier,
                    imageUrl = item.imageUrl,
                    writerName = item.authorName,
                    time = gapBetweenNow(time = item.createdAt),
                    onTap = { onTapContent(item.postId) },
                    isMission = false,
                )
            }
        }
    }
}

@Composable
fun MissionFeedTab(
    postItems: List<MainPageFeedModel>,
    isMissionUnlocked: Boolean,
    onTapContent: (String) -> Unit = {},
) {
    if (postItems.isEmpty()) {
        Column(
            modifier = Modifier
                .height(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(if(isMissionUnlocked) R.drawable.bbibbi
                else R.drawable.chest_bibbi),
                contentDescription = null, // 필수 param
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )

        }
    } else {
        VerticalGrid {
            postItems.forEach { item ->
                HomePageFeedElement(
                    modifier = Modifier,
                    imageUrl = item.imageUrl,
                    writerName = item.authorName,
                    time = gapBetweenNow(time = item.createdAt),
                    onTap = { onTapContent(item.postId) },
                    isMission = true,
                )
            }


        }
    }
}

@Composable
fun SurvivalTextDescription(warningState: MutableState<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
    ) {
        Text(
            text = if (warningState.value == 1)
                stringResource(id = R.string.home_time_not_much)
            else
                stringResource(id = R.string.home_image_on_duration),
            color = MaterialTheme.bbibbiScheme.textSecondary,
            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
        )

        if (warningState.value == 1) {
            Image(
                painter = painterResource(id = R.drawable.fire_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.smile_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MissionTextDescription(
    warningState: MutableState<Int>,
    isMissionUnlocked: Boolean,
    missionText: String,
    remainingMemberCnt: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
    ) {
        val missionWaitingText = buildAnnotatedString {
            append("가족 중 ")
            withStyle(style = SpanStyle(MaterialTheme.bbibbiScheme.mainYellow)) {
                append("${remainingMemberCnt}명")
            }
            append("만 더 올리면 미션 열쇠를 받아요!")
        }
        if(isMissionUnlocked) {
            Text(
                text = missionText,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
        } else {
            Text(
                text = missionWaitingText,
                color = MaterialTheme.bbibbiScheme.textSecondary,
                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            )
        }


        if (isMissionUnlocked) {
            Image(
                painter = painterResource(id = R.drawable.smile_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.key_icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
