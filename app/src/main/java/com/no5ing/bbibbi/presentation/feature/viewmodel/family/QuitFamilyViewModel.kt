package com.no5ing.bbibbi.presentation.feature.viewmodel.family

import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.feature.viewmodel.BaseViewModel
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class QuitFamilyViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
) : BaseViewModel<APIResponse<DefaultResponse>>() {
    override fun initState(): APIResponse<DefaultResponse> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            setState(
                restAPI.getMemberApi()
                    .quitFamily()
                    .updateMyFamilyInfo()
                    .wrapToAPIResponse()
            )
        }
    }

    private suspend fun ApiResponse<DefaultResponse>.updateMyFamilyInfo() = this.suspendOnSuccess {
        restAPI.getMemberApi().getMeInfo().suspendOnSuccess {
            sessionModule.onLoginWithCredentials(
                newTokenPair = sessionModule.sessionState.value.apiToken,
                member = data,
            )
        }.suspendOnFailure {
            sessionModule.invalidateSession()
        }
    }

}