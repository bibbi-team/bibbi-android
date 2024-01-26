package com.no5ing.bbibbi.presentation.feature.viewmodel.post

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.post.CalendarBanner
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MonthlyStatisticsViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<CalendarBanner>>() {
    override fun initState(): APIResponse<CalendarBanner> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        val yearMonth = arguments.get("yearMonth") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            val result = restAPI.getPostApi().getCalendarBanner(
                yearMonth = yearMonth,
            ).wrapToAPIResponse()
            setState(result)
        }
    }

}