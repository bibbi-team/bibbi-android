package com.no5ing.bbibbi.presentation.viewmodel.family

import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.JoinFamilyRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class FamilyRegistrationViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Family>>() {
    val hasRegistrationToken = isReLogin() ||
            localDataStorage.hasRegistrationToken()

    private fun isReLogin(): Boolean {
        return sessionModule.sessionState.value.hasFamily()
    }

    override fun initState(): APIResponse<Family> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            if (isReLogin()) {
                Timber.d("[FamilyReg] Already has Family!")
                setState(
                    APIResponse.success(
                        Family(
                            familyID = sessionModule.sessionState.value.familyId,
                            createdAt = ZonedDateTime.now()
                        )
                    )
                )
                return@withMutexScope
            }
            val registrationToken = localDataStorage.getAndDeleteRegistrationToken()
            if (registrationToken == null) {
                //링크 타고 온게 아니라 그냥 여까지 가입한사람
                setState(
                    restAPI.getMemberApi()
                        .createAndJoinFamily()
                        .updateMyFamilyInfo()
                        .wrapToAPIResponse()
                )
            } else {
                setState(
                    restAPI.getMemberApi().joinFamilyWithToken(
                        JoinFamilyRequest(
                            inviteCode = registrationToken
                        )
                    )
                        .updateMyFamilyInfo()
                        .wrapToAPIResponse()
                )
            }

        }
    }

    private suspend fun ApiResponse<Family>.updateMyFamilyInfo() = this.suspendOnSuccess {
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