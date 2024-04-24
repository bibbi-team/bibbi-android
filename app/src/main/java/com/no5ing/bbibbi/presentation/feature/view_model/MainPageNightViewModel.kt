package com.no5ing.bbibbi.presentation.feature.view_model

import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.view.NightMainPageModel
import com.no5ing.bbibbi.data.repository.Arguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainPageNightViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<NightMainPageModel>>() {

    override fun initState(): APIResponse<NightMainPageModel> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val mainResult = restAPI.getViewApi().getNightMainView()
            val apiResult = mainResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}