package com.no5ing.bbibbi.presentation.ui.feature.main.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.viewmodel.post.CalendarMonthViewModel
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.selection.EmptySelectionState
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MainCalendarPage(
    initialYearMonth: YearMonth = YearMonth.now(),
    onDispose: () -> Unit = {},
    onTapDay: (LocalDate) -> Unit = {},
    calendarMonthViewModel: CalendarMonthViewModel = hiltViewModel(),
) {
    val currentCalendarState: CalendarState<EmptySelectionState> = remember {
        CalendarState(
            selectionState = EmptySelectionState,
            monthState = MonthState(
                initialMonth = initialYearMonth,
            )
        )
    }
    val uiState = calendarMonthViewModel.uiState.collectAsState()

    LaunchedEffect(currentCalendarState.monthState.currentMonth) {
        Timber.d("[MainCalendarPage] Changed month!")
        calendarMonthViewModel.invoke(
            Arguments(
                arguments = mapOf("yearMonth" to currentCalendarState.monthState.currentMonth.toString()),
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.calendar)
            )
            Spacer(modifier = Modifier.height(12.dp))
            MainCalendarYearMonthBar(
                yearMonthState = currentCalendarState.monthState.currentMonth
            )
            StaticCalendar(
                calendarState = currentCalendarState,
                monthHeader = {},
                daysOfWeekHeader = { daysOfWeek ->
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        daysOfWeek.forEach { dayOfWeek ->
                            Text(
                                textAlign = TextAlign.Center,
                                text = dayOfWeek.getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.getDefault()
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                },
                dayContent = {
                    MainCalendarDay(
                        state = it,
                        monthState = uiState.value,
                        onClick = { date ->
                            if (uiState.value.containsKey(date)) {
                                onTapDay(date)
                            }
                        },
                    )
                },
            )
        }
    }
}

@Composable
fun MainCalendarYearMonthBar(
    yearMonthState: YearMonth,
) {
    val balloonColor = MaterialTheme.colorScheme.surface
    val balloonText = stringResource(id = R.string.calendar_everyday_info)
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
        // setBackgroundColorResource(balloonColor)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }

    val yearStr = stringResource(id = R.string.year)
    val monthStr = stringResource(id = R.string.month)
    Row(
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${yearMonthState.year}${yearStr} ${yearMonthState.month.value}${monthStr}",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.secondary
        )
        Balloon(
            builder = builder,
            balloonContent = {
                Text(
                    text = balloonText,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.warning_circle_icon),
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { it.showAlignBottom() }
            )

        }

    }

}