package com.no5ing.bbibbi.presentation.viewmodel.auth

import com.google.firebase.messaging.FirebaseMessaging
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.datasource.network.request.member.QuitMemberRequest
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.APIResponse.Companion.idle
import com.no5ing.bbibbi.data.model.APIResponse.Companion.wrapToAPIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class QuitViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
) : BaseViewModel<APIResponse<DefaultResponse>>() {
    override fun initState(): APIResponse<DefaultResponse> {
        return idle()
    }

    override fun invoke(arguments: Arguments) {
        val reasons = arguments.get("reasons")
        val reasonList = reasons?.split(",") ?: emptyList()
        withMutexScope(Dispatchers.IO) {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            restAPI.getMemberApi().deleteFcmToken(fcmToken)
            val result = restAPI.getMemberApi().quitMember(
                sessionModule.sessionState.value.memberId,
                QuitMemberRequest(
                    reasonIds = reasonList,
                )
            ).suspendOnSuccess {
                sessionModule.invalidateSession()
            }.wrapToAPIResponse()
            setState(result)
        }
    }
}