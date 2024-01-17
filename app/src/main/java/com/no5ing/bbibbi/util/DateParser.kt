package com.no5ing.bbibbi.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun gapBetweenNow(time: Instant): String {
    val now = Instant.now()
    val gap = now.epochSecond - time.epochSecond
    return when {
        gap < 60 -> stringResource(id = R.string.time_now)
        gap < 3600 -> "${gap / 60}${stringResource(id = R.string.time_minutes)}"
        gap < 86400 -> "${gap / 3600}${stringResource(id = R.string.time_hours)}"
        gap < 2592000 -> "${gap / 86400}${stringResource(id = R.string.time_days)}"
        gap < 31536000 -> "${gap / 2592000}${stringResource(id = R.string.time_months)}"
        else -> "${gap / 31536000}${stringResource(id = R.string.time_years)}"
    }
}

@Composable
fun gapBetweenNow(time: ZonedDateTime) = gapBetweenNow(time.toInstant())

private val yearDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
private val dateFormatter = DateTimeFormatter.ofPattern("MM월 dd일")
fun toLocalizedDate(time: ZonedDateTime): String {
    if (time.year == ZonedDateTime.now().year)
        return dateFormatter.format(time)
    return yearDateFormatter.format(time)
}

fun todayAsString() = LocalDate.now().toString()

fun getZonedDateTimeString() = ZonedDateTime.now().toString()

fun LocalDate.weekOfMonth(): Int {
    val weekFields = WeekFields.of(Locale.getDefault())
    return this.get(weekFields.weekOfMonth())
}

fun LocalDate.toYearMonth() = YearMonth.of(this.year, this.month)

fun LocalDate.weekDates(): List<LocalDate> {
    val weekFields = WeekFields.of(Locale.FRANCE)
    val firstDayOfWeek = weekFields.firstDayOfWeek
    val firstDayOfThisWeek = this.with(firstDayOfWeek)
    return listOf(firstDayOfThisWeek)
        .fillUpTo(firstDayOfThisWeek.plusDays((7 - 1).toLong()))
}

internal fun Collection<LocalDate>.fillUpTo(date: LocalDate) =
    (0..date.toEpochDay() - first().toEpochDay()).map {
        first().plusDays(it)
    }

fun LocalDate.isBirthdayNow(): Boolean {
    val now = LocalDate.now()
    return this.monthValue == now.monthValue && this.dayOfMonth == now.dayOfMonth
}