package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.no5ing.bbibbi.util.toYearMonth
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.util.TreeMap
import javax.inject.Inject

@HiltViewModel
class CalendarWeekViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<TreeMap<LocalDate, CalendarElement>>() {
    private val queriedMonths = mutableSetOf<YearMonth>()
    override fun initState(): TreeMap<LocalDate, CalendarElement> {
        return TreeMap()
    }

    override fun invoke(arguments: Arguments) {
        val targetDate = arguments.get("date") ?: throw RuntimeException()
        val startDate = LocalDate.parse(targetDate).toYearMonth()
        val backMonth = startDate.minusMonths(1)
        val nextMonth = startDate.plusMonths(1)

        Timber.d("Invoke date: $startDate")
        withMutexScope(Dispatchers.IO) {
            val priorMap = TreeMap(uiState.value)
            retrieveAndAppend(backMonth, priorMap)
            retrieveAndAppend(startDate, priorMap)
            retrieveAndAppend(nextMonth, priorMap)
            setState(priorMap)
        }
    }

    private suspend fun retrieveAndAppend(
        yearMonth: YearMonth,
        priorMap: MutableMap<LocalDate, CalendarElement>
    ) {
        if (queriedMonths.contains(yearMonth)) return
        queriedMonths.add(yearMonth)
        restAPI.getPostApi().getMonthlyCalendar(
            yearMonth = yearMonth.toString(),
        ).suspendOnSuccess {
            data.results.forEach {
                val date = LocalDate.parse(it.date)
                priorMap[date] = it
            }
        }
    }

}