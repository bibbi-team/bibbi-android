package com.no5ing.bbibbi.presentation.viewmodel.post

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarWeekViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<Map<LocalDate, CalendarElement>>() {
    private val mutex = Mutex()
    override fun initState(): Map<LocalDate, CalendarElement> {
        return emptyMap()
    }

    override fun invoke(arguments: Arguments) {
        val yearMonth = arguments.get("yearMonth") ?: throw RuntimeException()
        val week = arguments.get("week")?.toIntOrNull() ?: throw RuntimeException()
        viewModelScope.launch(Dispatchers.IO) {
            mutex.withLock {
                val priorMap = uiState.value.toMutableMap()
                restAPI.getPostApi().getWeeklyCalendar(
                    yearMonth = yearMonth,
                    week = week,
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

}