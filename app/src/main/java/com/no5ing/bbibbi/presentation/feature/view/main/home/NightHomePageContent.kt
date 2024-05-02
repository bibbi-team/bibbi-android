package com.no5ing.bbibbi.presentation.feature.view.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.post.PostType
import com.no5ing.bbibbi.data.model.view.MainPageMonthlyRankerModel
import com.no5ing.bbibbi.data.model.view.MainPageTopBarModel
import com.no5ing.bbibbi.data.model.view.NightMainPageModel
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.component.button.FlatButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun NightHomePageContent(
    mainPageState: StateFlow<APIResponse<NightMainPageModel>>,
    deferredPickStateSet: StateFlow<Set<String>>,
    postViewTypeState: MutableState<PostType> = remember { mutableStateOf(PostType.SURVIVAL) },
    onTapViewPost: (LocalDate) -> Unit = {},
    onTapProfile: (String) -> Unit = {},
    onTapPick: (MainPageTopBarModel) -> Unit = {},
    onTapInvite: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val warningState = remember {
        mutableIntStateOf(0)
    }
    val balloonColor = MaterialTheme.bbibbiScheme.button
    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setMarginTop(12)
        setPaddingVertical(10)
        setPaddingHorizontal(16)
        setMarginHorizontal(12)
        setCornerRadius(12f)
        setBackgroundColor(balloonColor)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }

    val mainPageModel by mainPageState.collectAsState()
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
            SurvivalTextDescription(warningState = warningState)
            Spacer(modifier = Modifier.height(24.dp))
            if (mainPageModel.isReady()) {
                val ranking = mainPageModel.data.familyMemberMonthlyRanking
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(
                            MaterialTheme.bbibbiScheme.backgroundSecondary,
                            RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                            // .padding(horizontal = 20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.home_max_monthly_contributor),
                                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                                    color = MaterialTheme.bbibbiScheme.textPrimary,
                                )
                                Balloon(
                                    builder = builder,
                                    balloonContent = {
                                        Text(
                                            text = stringResource(id = R.string.home_contributor_baloon),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.bbibbiScheme.white,
                                            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.warning_circle_icon),
                                        tint = MaterialTheme.bbibbiScheme.textSecondary,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { it.showAlignBottom() }
                                    )
                                }
                            }

                            Text(
                                text = stringResource(id = R.string.home_max_monthly_contributor_month, ranking.month),
                                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                                color = MaterialTheme.bbibbiScheme.textSecondary,
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Box(
                                Modifier.padding(top = 36.dp)
                            ) {
                                RankIcon(
                                    model = ranking.secondRanker,
                                    outerCircleSize = 72.dp,
                                    innerCircleSize = 64.dp,
                                    borderColor = getRankColor(rank = 1)!!,
                                    noRankImage = painterResource(id = R.drawable.second_ranking_empty),
                                    rankImage = painterResource(id = R.drawable.second_ranking),
                                    badgeHeight = 27.dp
                                )
                            }

                            RankIcon(
                                model = ranking.firstRanker,
                                outerCircleSize = 90.dp,
                                innerCircleSize = 82.dp,
                                borderColor = getRankColor(rank = 0)!!,
                                noRankImage = painterResource(id = R.drawable.first_ranking_empty),
                                rankImage = painterResource(id = R.drawable.first_ranking),
                                badgeHeight = 32.dp
                            )
                            Box(
                                Modifier.padding(top = 36.dp)
                            ) {
                                RankIcon(
                                    model = ranking.thirdRanker,
                                    outerCircleSize = 72.dp,
                                    innerCircleSize = 64.dp,
                                    borderColor = getRankColor(rank = 2)!!,
                                    noRankImage = painterResource(id = R.drawable.third_ranking_empty),
                                    rankImage = painterResource(id = R.drawable.third_ranking),
                                    badgeHeight = 27.dp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(36.dp))
                        if (ranking.isAllRankersNull()) {
                            Text(
                                text = stringResource(id = R.string.home_no_before_survival),
                                style = MaterialTheme.bbibbiTypo.bodyOneBold,
                                color = MaterialTheme.bbibbiScheme.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            FlatButton(
                                text = stringResource(id = R.string.home_see_before_survival),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    ranking.mostRecentSurvivalPostDate?.apply(onTapViewPost)
                                },
                            )
                        }
                    }
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
fun RankIcon(
    model: MainPageMonthlyRankerModel?,
    outerCircleSize: Dp,
    innerCircleSize: Dp,
    borderColor: Color,
    noRankImage: Painter,
    rankImage: Painter,
    badgeHeight: Dp,
) {
    if (model == null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    Modifier
                        .size(outerCircleSize)
                        .background(MaterialTheme.bbibbiScheme.gray600, CircleShape)
                )
                Box(
                    Modifier
                        .size(innerCircleSize)
                        .background(MaterialTheme.bbibbiScheme.backgroundHover, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "?", style = TextStyle(
                            fontSize = 33.75.sp,
                            fontWeight = FontWeight.SemiBold
                        ), color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Image(
                    painter = noRankImage,
                    contentDescription = null,
                    modifier = Modifier
                        .height(badgeHeight)
                        .align(Alignment.BottomCenter)
                        .offset(y = 12.dp)
                )
            }
            CommonEmptyBlock()
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    Modifier
                        .size(outerCircleSize)
                        .background(borderColor, CircleShape)
                )
                CircleProfileImage(
                    size = innerCircleSize,
                    noImageLetter = model.name.first().toString(),
                    imageUrl = model.profileImageUrl
                )
                Image(
                    painter = rankImage,
                    contentDescription = null,
                    modifier = Modifier
                        .height(badgeHeight)
                        .align(Alignment.BottomCenter)
                        .offset(y = 12.dp)
                )
            }
            CommonTextBlock(
                name = model.name,
                count = model.survivalCount
            )
        }
    }


}

@Composable
fun CommonEmptyBlock() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 53.dp, height = 16.dp)
                .background(MaterialTheme.bbibbiScheme.gray600, RoundedCornerShape(4.dp))
        )
        Box(
            modifier = Modifier
                .size(width = 29.dp, height = 12.dp)
                .background(MaterialTheme.bbibbiScheme.button, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun CommonTextBlock(
    name: String,
    count: Int,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            style = MaterialTheme.bbibbiTypo.bodyTwoBold,
            color = MaterialTheme.bbibbiScheme.textPrimary,
        )
        Text(
            text = "${count}회",
            style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
            color = MaterialTheme.bbibbiScheme.textSecondary,
        )
    }
}