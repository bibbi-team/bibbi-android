package com.no5ing.bbibbi.presentation.ui.feature.main.calendar.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import com.no5ing.bbibbi.presentation.viewmodel.post.AddPostReactionViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.CalendarWeekViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.FamilyPostViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.PostReactionBarViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.RemovePostReactionViewModel
import com.no5ing.bbibbi.util.toYearMonth
import com.no5ing.bbibbi.util.weekDates
import com.no5ing.bbibbi.util.weekOfMonth
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.WeekCalendarState
import io.github.boguszpawlowski.composecalendar.header.WeekState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.week.Week
import timber.log.Timber
import java.time.LocalDate

@Composable
fun CalendarDetailPage(
    initialDay: LocalDate,
    onDispose: () -> Unit,
    onTapProfile: (Member) -> Unit,
    familyPostViewModel: FamilyPostViewModel = hiltViewModel(),
    familyPostReactionBarViewModel: PostReactionBarViewModel = hiltViewModel(),
    removePostReactionViewModel: RemovePostReactionViewModel = hiltViewModel(),
    addPostReactionViewModel: AddPostReactionViewModel = hiltViewModel(),
    calendarWeekViewModel: CalendarWeekViewModel = hiltViewModel(),
) {
    val postState = familyPostViewModel.uiState.collectAsState()
    val uiState = calendarWeekViewModel.uiState.collectAsState()
    val currentCalendarState = remember {
        WeekCalendarState<DynamicSelectionState>(
            weekState = WeekState(
                initialWeek = Week(initialDay.weekDates()),
            ),
            selectionState = DynamicSelectionState(
                selectionMode = SelectionMode.Single,
                selection = listOf(initialDay),
                confirmSelectionChange = {
                    Timber.d("YOU SELECTED!")
                    val selection = it.firstOrNull() ?: return@DynamicSelectionState false
                    uiState.value.containsKey(selection)
                }
            ),
        )
    }

    LaunchedEffect(currentCalendarState.weekState.currentWeek) {
        Timber.d("CHANGED WEEK!!")
        val start = currentCalendarState.weekState.currentWeek.start
        val startBefore = start.minusWeeks(1L)
        val startNext = start.plusWeeks(1L)
        calendarWeekViewModel.invoke(
            Arguments(
                arguments = mapOf(
                    "yearMonth" to start.toYearMonth().toString(),
                    "week" to start.weekOfMonth().toString(),
                ),
            )
        )
        if (start.toYearMonth() != startBefore.toYearMonth()
            || start.weekOfMonth() != startBefore.weekOfMonth()
        ) {
            calendarWeekViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "yearMonth" to startBefore.toYearMonth().toString(),
                        "week" to startBefore.weekOfMonth().toString(),
                    ),
                )
            )
        }
        if (start.toYearMonth() != startNext.toYearMonth()
            || start.weekOfMonth() != startNext.weekOfMonth()
        ) {
            calendarWeekViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "yearMonth" to startNext.toYearMonth().toString(),
                        "week" to startNext.weekOfMonth().toString(),
                    ),
                )
            )
        }
    }

    val currentSelection = currentCalendarState.selectionState.selection.first()
    LaunchedEffect(uiState.value[currentSelection], currentSelection) {

        val postId = uiState.value[currentSelection]?.representativePostId ?: return@LaunchedEffect
        familyPostViewModel.invoke(
            Arguments(
                resourceId = postId,
            )
        )
    }
    val yearStr = stringResource(id = R.string.year)
    val monthStr = stringResource(id = R.string.month)
    val currentYearMonth = currentCalendarState.weekState.currentWeek.yearMonth
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = postState.value.isReady(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box {
                AsyncImage(
                    model = postState.value.data.post.imageUrl,
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
                if (postState.value.isReady()) {
                    AnimatedContent(
                        targetState = postState.value,
                        transitionSpec = {
                            val direction = this.initialState.data.post.createdAt
                                .isAfter(this.targetState.data.post.createdAt)
                            (slideInHorizontally { if (direction) -it else it } togetherWith slideOutHorizontally { if (direction) it else -it })
                                .using(
                                    SizeTransform(clip = false)
                                )
                        }, label = ""
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            PostViewDetailTopBar(
                                member = it.data.writer,
                                onTap = {
                                    onTapProfile(it.data.writer)
                                }
                            )
                            PostViewContent(
                                post = it.data.post,
                                familyPostReactionBarViewModel = familyPostReactionBarViewModel,
                                removePostReactionViewModel = removePostReactionViewModel,
                                addPostReactionViewModel = addPostReactionViewModel,
                            )
                        }
                    }

                }
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
            modifier = Modifier.size(35.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = member.name,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}