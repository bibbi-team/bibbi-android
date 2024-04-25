package com.no5ing.bbibbi.presentation.feature.view_model.mission

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.mission.Mission
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class GetTodayMissionViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<Mission>>() {
    override fun initState(): APIResponse<Mission> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val meResult = restAPI.getPostApi().getDailyMission()
            val apiResult = meResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}