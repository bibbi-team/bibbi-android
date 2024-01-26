package com.no5ing.bbibbi.presentation.feature.viewmodel.auth

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.auth.AppVersion
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class RetrieveAppVersionViewModel @Inject constructor(
    private val restAPI: RestAPI,
) : BaseViewModel<APIResponse<AppVersion>>() {
    override fun initState(): APIResponse<AppVersion> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO) {
            val meResult = restAPI.getMemberApi().getAppVersion()
            val apiResult = meResult.wrapToAPIResponse()
            setState(apiResult)
        }
    }
}