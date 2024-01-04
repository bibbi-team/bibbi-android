package com.no5ing.bbibbi.presentation.viewmodel.family

import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.JoinFamilyRequest
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FamilyRegistrationViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<APIResponse<Family>>() {
    override fun initState(): APIResponse<Family> {
        return APIResponse.idle()
    }

    override fun invoke(arguments: Arguments) {
        withMutexScope(Dispatchers.IO, uiState.value.isIdle()) {
            val registrationToken = localDataStorage.getAndDeleteRegistrationToken()
            if (registrationToken == null) {
                //링크 타고 온게 아니라 그냥 여까지 가입한사람
                setState(restAPI.getMemberApi().createAndJoinFamily().wrapToAPIResponse())
            } else {
                setState(
                    restAPI.getMemberApi().joinFamilyWithToken(
                        JoinFamilyRequest(
                            inviteCode = registrationToken
                        )
                    ).wrapToAPIResponse()
                )
            }

        }
    }

}