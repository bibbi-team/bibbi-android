package com.no5ing.bbibbi.presentation.ui.feature.main.calendar.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.feature.main.calendar.MainCalendarDay
import com.no5ing.bbibbi.presentation.ui.feature.post.view.PostViewContent
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.CalendarDetailContentViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.CalendarWeekViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.CalenderDetailContentUiState
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.weekDates
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.WeekCalendarState
import io.github.boguszpawlowski.composecalendar.header.WeekState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.week.Week
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarDetailPage(
    initialDay: LocalDate,
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    //familyPostViewModel: FamilyPostViewModel = hiltViewModel(),
    calendarDetailContentViewModel: CalendarDetailContentViewModel = hiltViewModel(),
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    calendarWeekViewModel: CalendarWeekViewModel = hiltViewModel(),
) {
    // val postState = familyPostViewModel.uiState.collectAsState()
    val snackBarState = LocalSnackbarHostState.current
    val uiState = calendarWeekViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { 3 },
        initialPage = 1,
    )
    val scrollEnabled = remember {
        mutableStateOf(true)
    }
    val calendarDetailState = calendarDetailContentViewModel.uiState.collectAsState()
    val currentPostState = remember {
        mutableStateOf(CalenderDetailContentUiState(null, null, null))
    }
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

    val currentSelection = currentCalendarState.selectionState.selection.first()
    LaunchedEffect(uiState.value[currentSelection], currentSelection) {
        val uiValue = uiState.value[currentSelection] ?: return@LaunchedEffect
        val uiStateList = uiState.value.toList()
        val centerIdx = uiStateList.indexOf(currentSelection to uiValue)

        val left = uiStateList.getOrNull(centerIdx - 1)
        val right = uiStateList.getOrNull(centerIdx + 1)

        Timber.d("[CalendarDetailPage] Invoking detail content with idx = $centerIdx")
        calendarDetailContentViewModel.invoke(
            Arguments(
                resourceId = uiValue.representativePostId,
                arguments = mapOf(
                    "left" to left?.second?.representativePostId,
                    "right" to right?.second?.representativePostId,
                )
            )
        )
    }

    LaunchedEffect(calendarDetailState.value) {
        val actualValue = calendarDetailState.value
        if (actualValue.isReady()) {
            Timber.d("[CalendarDetailPage] hasLeft = ${actualValue.data.first != null}, hasRight = ${actualValue.data.third != null}")
            currentPostState.value = actualValue.data
            pagerState.scrollToPage(1)
            calendarDetailContentViewModel.resetState()
            scrollEnabled.value = true

        }
    }

    val noMoreItemMessage = stringResource(id = R.string.no_more_calendar_items)
    LaunchedEffect(pagerState.currentPage) {
        Timber.d("[CalendarDetailPage] CurrentPage: ${pagerState.currentPage}")
        if (pagerState.currentPage != 1) {
            scrollEnabled.value = false
            val item = when (pagerState.currentPage) {
                0 -> currentPostState.value.first
                2 -> currentPostState.value.third
                else -> null
            }
            item?.let { newItem ->
                val newItemDate = newItem.post.createdAt.toLocalDate()
                Timber.d("[CalendarDetailPage] New Item Date: $newItemDate")
                currentCalendarState.selectionState.onDateSelected(newItemDate)
                currentCalendarState.weekState.currentWeek = Week(newItemDate.weekDates())
            } ?: Unit.apply {
                snackBarState.showSnackBarWithDismiss(
                    noMoreItemMessage,
                    snackBarWarning
                )
                coroutineScope.launch {
                    delay(50L)
                    pagerState.animateScrollToPage(1)
                    scrollEnabled.value = true

                }

            }
        }

    }
    val yearStr = stringResource(id = R.string.year)
    val monthStr = stringResource(id = R.string.month)
    val currentYearMonth = currentCalendarState.weekState.currentWeek.yearMonth

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = currentPostState.value.second != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box {
                AsyncImage(
                    model = currentPostState.value.second?.post?.imageUrl,
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
                    title = "${currentYearMonth.year}${yearStr} ${currentYearMonth.month.value}${monthStr}",
                )
                Spacer(modifier = Modifier.height(12.dp))
                SelectableWeekCalendar(
                    calendarState = currentCalendarState,
                    dayContent = { dayState ->
                        MainCalendarDay(
                            state = dayState,
                            monthState = uiState.value,
                            onClick = {
                                dayState.selectionState.onDateSelected(it)
                            }
                        )
                    },
                    weekHeader = {},
                    daysOfWeekHeader = {}
                )


//                    AnimatedContent(
//                        targetState = postState.value,
//                        transitionSpec = {
//                            val direction = this.initialState.data.post.createdAt
//                                .isAfter(this.targetState.data.post.createdAt)
//                            (slideInHorizontally { if (direction) -it else it } togetherWith slideOutHorizontally { if (direction) it else -it })
//                                .using(
//                                    SizeTransform(clip = false)
//                                )
//                        }, label = ""
//                    ) {

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = scrollEnabled.value,
                ) { index ->
                    val item = when (index) {
                        0 -> currentPostState.value.first
                        1 -> currentPostState.value.second
                        2 -> currentPostState.value.third
                        else -> null
                    }
                    Column(
                        modifier = Modifier
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (item != null) {
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
                            )
                        }
                    }
                }

                //   }


            }
        }

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
            size = 35.dp,
            onTap = onTap,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = member.name,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}