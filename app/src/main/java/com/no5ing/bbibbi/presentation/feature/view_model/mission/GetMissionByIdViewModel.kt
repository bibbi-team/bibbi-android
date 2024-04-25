package com.no5ing.bbibbi.presentation.feature.view_model.mission

import com.no5ing.bbibbi.data.datasource.local.MissionCacheProvider
import com.no5ing.bbibbi.data.model.mission.Mission
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class GetMissionByIdViewModel @Inject constructor(
    private val missionCacheProvider: MissionCacheProvider,
) : BaseViewModel<Mission?>() {
    override fun initState(): Mission? {
        return null
    }

    override fun invoke(arguments: Arguments) {
        val missionId = arguments.resourceId ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            setState(missionCacheProvider.getMission(missionId))
        }
    }
}