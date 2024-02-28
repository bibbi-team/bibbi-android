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

private fun getYearMonthPattern(): String {
    return when (Locale.getDefault()) {
        Locale.KOREA -> "yyyy년 MM월"
        Locale.ENGLISH -> "yyyy, MMMM"
        else -> "yyyy/MM"
    }
}

private fun getSameYearMonthPattern(): String {
    return when (Locale.getDefault()) {
        Locale.KOREA -> "MM월"
        Locale.ENGLISH -> "yyyy, MMMM"
        else -> "yyyy/MM"
    }
}


private fun getYearDatePattern(): String {
    return when (Locale.getDefault()) {
        Locale.KOREA -> "yyyy년 MM월 dd일"
        Locale.ENGLISH -> "yyyy, MMMM dd"
        else -> "yyyy/MM/dd"
    }
}

private fun getDatePattern(): String {
    return when (Locale.getDefault()) {
        Locale.KOREA -> "MM월 dd일"
        Locale.ENGLISH -> "MMMM dd"
        else -> "MM/dd"
    }
}

private val yearMonthFormatter = DateTimeFormatter.ofPattern(getYearMonthPattern())
private val sameYearMonthFormatter = DateTimeFormatter.ofPattern(getSameYearMonthPattern())
private val yearDateFormatter = DateTimeFormatter.ofPattern(getYearDatePattern())
private val dateFormatter = DateTimeFormatter.ofPattern(getDatePattern())

fun toLocalizedDate(time: ZonedDateTime): String {
    if (time.year == ZonedDateTime.now().year)
        return dateFormatter.format(time)
    return yearDateFormatter.format(time)
}

fun toLocalizedDate(date: String): String {
    val localDate = LocalDate.parse(date)
    if (localDate.year == ZonedDateTime.now().year)
        return dateFormatter.format(localDate)
    return yearDateFormatter.format(localDate)
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

fun formatYearMonth(year: Int, month: Int): String {
    val isSameYear = year == ZonedDateTime.now().year
    val currentYearMonth = YearMonth.of(year, month)
    return if(isSameYear)
        currentYearMonth.format(sameYearMonthFormatter)
        else currentYearMonth.format(yearMonthFormatter)
}
