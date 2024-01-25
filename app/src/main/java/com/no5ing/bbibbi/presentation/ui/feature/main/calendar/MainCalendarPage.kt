package com.no5ing.bbibbi.presentation.ui.feature.main.calendar

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.post.CalendarBanner
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.post.CalendarMonthViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.MonthlyStatisticsViewModel
import com.no5ing.bbibbi.util.getScreenSize
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
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MainCalendarPage(
    initialYearMonth: YearMonth = remember { YearMonth.now() },
    onDispose: () -> Unit = {},
    onTapDay: (LocalDate) -> Unit = {},
    calendarMonthViewModel: CalendarMonthViewModel = hiltViewModel(),
    monthlyStatisticsViewModel: MonthlyStatisticsViewModel = hiltViewModel(),
) {
    val (width, height) = getScreenSize()
    val currentCalendarState: CalendarState<EmptySelectionState> = remember {
        CalendarState(
            selectionState = EmptySelectionState,
            monthState = MonthState(
                initialMonth = initialYearMonth,
            )
        )
    }
    val uiState = calendarMonthViewModel.uiState.collectAsState()
    val statState by monthlyStatisticsViewModel.uiState.collectAsState()

    LaunchedEffect(currentCalendarState.monthState.currentMonth) {
        Timber.d("[MainCalendarPage] Changed month!")
        calendarMonthViewModel.invoke(
            Arguments(
                arguments = mapOf("yearMonth" to currentCalendarState.monthState.currentMonth.toString()),
            )
        )
        monthlyStatisticsViewModel.invoke(
            Arguments(
                arguments = mapOf("yearMonth" to currentCalendarState.monthState.currentMonth.toString()),
            )
        )
    }

    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.calendar)
            )
            Spacer(modifier = Modifier.height(12.dp))
            MainCalendarYearMonthBar(
                yearMonthState = currentCalendarState.monthState.currentMonth,
                statisticsState = monthlyStatisticsViewModel.uiState,
            )
            if (statState.isReady()) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = resolveBannerImageByName(statState.data.bannerImageType)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (width - 40.dp).times(0.07462686f),
                                y = (width - 40.dp).times(0.06865671f),
                            )
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.calendar_banner_title_top),
                                    color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                    style = MaterialTheme.bbibbiTypo.caption
                                        .copy(lineHeight = 24.sp),
                                    modifier = Modifier.alignByBaseline(),
                                )
                                Text(
                                    text = statState.data.familyTopPercentage.toString(),
                                    color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                    style = MaterialTheme.bbibbiTypo.headOne
                                        .copy(fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
                                    modifier = Modifier.alignByBaseline(),
                                )
                                Text(
                                    text = stringResource(id = R.string.calendar_banner_title_percent),
                                    color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                    style = MaterialTheme.bbibbiTypo.headTwoBold
                                        .copy(lineHeight = 24.sp),
                                    modifier = Modifier.alignByBaseline(),
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = stringResource(
                                    id = R.string.calendar_banner_subtitle,
                                    statState.data.allFamilyMembersUploadedDays
                                ),
                                color = MaterialTheme.bbibbiScheme.backgroundPrimary,
                                style = MaterialTheme.bbibbiTypo.caption,
                            )
                        }


                    }
                }

            }
            Spacer(modifier = Modifier.height(24.dp))
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
                                color = MaterialTheme.bbibbiScheme.textSecondary,
                                style = MaterialTheme.bbibbiTypo.caption,
                            )
                        }
                    }
                },
                dayContent = {
                    MainCalendarDay(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp)
                            .aspectRatio(1.0f),
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

private fun resolveBannerImageByName(bannerName: String): Int {
    return when (bannerName) {
        "SKULL_FLAG" -> {
            R.drawable.calendar_99
        }

        "ALONE_WALING" -> {
            R.drawable.calendar_66
        }

        "WE_ARE_FRIENDS" -> {
            R.drawable.calendar_33
        }

        else -> {
            R.drawable.calendar_1
        }
    }
}

@Composable
fun MainCalendarYearMonthBar(
    yearMonthState: YearMonth,
    statisticsState: StateFlow<APIResponse<CalendarBanner>>,
) {
    val statistics by statisticsState.collectAsState()
    val balloonColor = MaterialTheme.bbibbiScheme.button
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
        modifier = Modifier
            .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${yearMonthState.year}${yearStr} ${yearMonthState.month.value}${monthStr}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.bbibbiTypo.headOne.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.bbibbiScheme.textPrimary
            )
            Balloon(
                builder = builder,
                balloonContent = {
                    Text(
                        text = balloonText,
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
        if (statistics.isReady()) {
            Text(
                text = stringResource(
                    id = R.string.calendar_history_cnt,
                    statistics.data.allFamilyMembersUploadedDays
                ),
                color = MaterialTheme.bbibbiScheme.textPrimary,
                style = MaterialTheme.bbibbiTypo.bodyOneRegular,
            )

        } else {
            Box {}
        }
    }

}