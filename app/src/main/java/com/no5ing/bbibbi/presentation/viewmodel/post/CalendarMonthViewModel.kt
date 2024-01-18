package com.no5ing.bbibbi.presentation.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarMonthViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Map<LocalDate, CalendarElement>>() {
    private val queriedYearMonths = mutableSetOf<String>()
    override fun initState(): Map<LocalDate, CalendarElement> {
        return emptyMap()
    }

    override fun invoke(arguments: Arguments) {
        val yearMonth = arguments.get("yearMonth") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO, !queriedYearMonths.contains(yearMonth)) {
            queriedYearMonths.add(yearMonth)
            val yearMonthInst = YearMonth.parse(yearMonth)
            val priorMap = HashMap(uiState.value)
            priorMap.entries.removeIf {
                it.key.year == yearMonthInst.year &&
                        it.key.monthValue == yearMonthInst.monthValue
            }
            restAPI.getPostApi().getMonthlyCalendar(
                yearMonth = yearMonth,
            ).suspendOnSuccess {
                data.results.forEach {
                    val date = LocalDate.parse(it.date)
                    priorMap[date] = it
                }
                setState(priorMap)
            }
        }
    }

}