package com.no5ing.bbibbi.presentation.feature.view_model

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.view.MainPageModel
import com.no5ing.bbibbi.data.repository.Arguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<MainPageModel>>() {
    override fun initState(): APIResponse<MainPageModel> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val mainResult = restAPI.getViewApi().getMainView()
            val apiResult = mainResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}