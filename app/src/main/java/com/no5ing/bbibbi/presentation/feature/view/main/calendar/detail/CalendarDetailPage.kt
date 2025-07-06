package com.no5ing.bbibbi.presentation.feature.view.main.calendar.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BannerAd
import com.no5ing.bbibbi.presentation.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarFire
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import com.no5ing.bbibbi.presentation.feature.view.main.calendar.MainCalendarDay
import com.no5ing.bbibbi.presentation.feature.view.main.post_view.PostViewContent
import com.no5ing.bbibbi.presentation.feature.view_model.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.CalendarWeekViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.FamilySwipePostsViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.formatYearMonth
import com.no5ing.bbibbi.util.getAdView
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.weekDates
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.WeekCalendarState
import io.github.boguszpawlowski.composecalendar.header.WeekState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.week.Week
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarDetailPage(
    initialDay: LocalDate,
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    calendarWeekViewModel: CalendarWeekViewModel = hiltViewModel(),
    familyPostsViewModel: FamilySwipePostsViewModel = hiltViewModel(),
) {
    val resources = localResources()
    val snackBarState = LocalSnackbarHostState.current
    val uiState = calendarWeekViewModel.uiState.collectAsState()
    val memberId = LocalSessionState.current.memberId
    val haptic = LocalHapticFeedback.current

    val scrollEnabled = remember {
        mutableStateOf(true)
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire_lottie))
    var playLottie by remember {
        mutableStateOf(false)
    }
    val progress = animateLottieCompositionAsState(composition, isPlaying = playLottie)
    val currentCalendarState = remember {
        WeekCalendarState(
            weekState = WeekState(
                initialWeek = Week(initialDay.weekDates()),
            ),
            selectionState = DynamicSelectionState(
                selectionMode = SelectionMode.Single,
                selection = listOf(initialDay),
                confirmSelectionChange = {
                    val selection = it.firstOrNull() ?: return@DynamicSelectionState false
                    uiState.value.containsKey(selection)
                }
            ),
        )
    }

    LaunchedEffect(progress.isAtEnd) {
        if (progress.isAtEnd) {
            playLottie = false
        }
    }

    LaunchedEffect(currentCalendarState.weekState.currentWeek) {
        Timber.d("[CalendarDetailPage] Changed week!")
        val start = currentCalendarState.weekState.currentWeek.start
        calendarWeekViewModel.invoke(
            Arguments(
                arguments = mapOf(
                    "date" to start.toString(),
                ),
            )
        )
    }

    val overScrollPagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    var overScrollEnabled by remember {
        mutableStateOf(true)
    }
    val currentSelection = currentCalendarState.selectionState.selection.first()
    LaunchedEffect(uiState.value[currentSelection], currentSelection) {
        val uiValue = uiState.value[currentSelection] ?: return@LaunchedEffect
        if (uiValue.allFamilyMembersUploaded) {
            launch {
                delay(500L)
                snackBarState.showSnackBarWithDismiss(
                    resources.getString(R.string.snack_bar_everyone_uploaded_day),
                    snackBarFire
                )
                playLottie = true
            }
        }

        familyPostsViewModel.invoke(
            Arguments(
                arguments = mapOf(
                    "date" to currentSelection.toString(),
                ),
            )
        )
    }

    val currentPostState by familyPostsViewModel.uiState.collectAsState()
    val adView = getAdView()


    val pagerState = key(currentPostState) {
        if (currentPostState.isReady()) {
            val index =
                0.coerceAtLeast(uiState.value[currentSelection]?.representativePostId?.let { postId ->
                    currentPostState.data.indexOfFirst { it.post.postId == postId }
                } ?: 0)
            rememberPagerState(
                pageCount = { currentPostState.data.size },
                initialPage = index,
            )
        } else {
            rememberPagerState(
                pageCount = { 1 },
                initialPage = 0,
            )
        }
    }

    LaunchedEffect(currentPostState, pagerState.currentPage) {
        if (currentPostState.isReady()) {
            if (overScrollPagerState.currentPage != 1)
                overScrollPagerState.scrollToPage(1)
            if (currentPostState.data.size <= pagerState.currentPage) {
                return@LaunchedEffect
            }
            familyPostReactionBarViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "postId" to currentPostState.data[pagerState.currentPage].post.postId,
                        "memberId" to memberId,
                    ),
                )
            )
        }
    }

    LaunchedEffect(overScrollPagerState) {
        snapshotFlow { overScrollPagerState.currentPage }.distinctUntilChanged()
            .collect { nextPage ->
                if (nextPage == 1) {
                    overScrollEnabled = true
                    return@collect
                }
                val currSelection = currentCalendarState.selectionState.selection[0]
                val currentWeeks = uiState.value.keys
                val nextDay = if (nextPage == 0) currentWeeks.filter { it.isBefore(currSelection) }
                    .maxWithOrNull(LocalDate::compareTo)
                else currentWeeks.filter { it.isAfter(currSelection) }
                    .minWithOrNull(LocalDate::compareTo)
                if (nextDay != null) {
                    currentCalendarState.selectionState.selection = listOf(nextDay)
                    currentCalendarState.weekState.currentWeek = Week(nextDay.weekDates())
                } else {
                    snackBarState.showSnackBarWithDismiss(
                        resources.getString(R.string.no_more_calendar_items),
                        snackBarWarning
                    )
                    overScrollEnabled = false
                    delay(250L)
                    overScrollPagerState.scrollToPage(1)
                }
            }
    }

    val currentYearMonth = currentCalendarState.weekState.currentWeek.yearMonth

    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Box {
            AnimatedVisibility(
                visible = currentPostState.isReady(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box {
                    AsyncImage(
                        model = asyncImagePainter(
                            source = currentPostState.data.getOrNull(
                                pagerState.currentPage
                            )?.post?.postImgUrl
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .blur(50.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.1f,
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    DisposableTopBar(
                        onDispose = onDispose,
                        title = formatYearMonth(
                            currentYearMonth.year,
                            currentYearMonth.month.value
                        ),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SelectableWeekCalendar(
                        calendarState = currentCalendarState,
                        dayContent = { dayState ->
                            MainCalendarDay(
                                state = dayState,
                                monthState = uiState.value,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dayState.selectionState.onDateSelected(it)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp)
                                    .aspectRatio(1.0f),
                            )
                        },
                        weekHeader = {},
                        daysOfWeekHeader = {}
                    )
                    if (currentPostState.isReady()) {
                        HorizontalPager(
                            state = overScrollPagerState,
                            userScrollEnabled = overScrollEnabled,
                        ) {
                            if (it == 1) {
                                CompositionLocalProvider(
                                    LocalOverscrollConfiguration provides null
                                ) {
                                    HorizontalPager(
                                        state = pagerState,
                                        userScrollEnabled = scrollEnabled.value,
                                    ) { index ->
                                        val item = currentPostState.data.getOrNull(index)
                                        Column(
                                            modifier = Modifier
                                        ) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            if (item != null) {
                                                CalendarDetailBody(
                                                    onTapProfile = onTapProfile,
                                                    onTapRealEmojiCreate = onTapRealEmojiCreate,
                                                    item = MainFeedUiState(
                                                        item.post.toPost(),
                                                        item.writer
                                                    ),
                                                    familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                                                    removePostReactionViewModel = removePostReactionViewModel,
                                                    addPostReactionViewModel = addPostReactionViewModel,
                                                    missionText = item.post.missionContent,
                                                )

                                            }
                                        }
                                    }
                                }

                            }

                        }

                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LottieAnimation(
                        composition,
                        progress = { progress.value },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
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
fun CalendarDetailBody(
    missionText: String? = null,
    onTapProfile: (Member) -> Unit,
    onTapRealEmojiCreate: (String) -> Unit,
    item: MainFeedUiState,
    familyPostReactionBarViewModel: PostReactionBarViewModel,
    removePostReactionViewModel: RemovePostReactionViewModel,
    addPostReactionViewModel: AddPostReactionViewModel,
) {
    Column {
        PostViewDetailTopBar(
            member = item.writer,
            onTap = {
                onTapProfile(item.writer)
            }
        )
        PostViewContent(
            post = item.post,
            familyPostReactionBarViewModel = familyPostReactionBarViewModel,
            removePostReactionViewModel = removePostReactionViewModel,
            addPostReactionViewModel = addPostReactionViewModel,
            onTapRealEmojiCreate = onTapRealEmojiCreate,
            missionText = missionText,
        )
    }
}


@Composable
fun PostViewDetailTopBar(
    onTap: () -> Unit,
    member: Member,
) {
    Row(
        modifier = Modifier
            .clickable {
                onTap()
            }
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleProfileImage(
            member = member,
            size = 36.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = member.name,
            color = MaterialTheme.bbibbiScheme.textPrimary,
            style = MaterialTheme.bbibbiTypo.caption,
        )
    }
}