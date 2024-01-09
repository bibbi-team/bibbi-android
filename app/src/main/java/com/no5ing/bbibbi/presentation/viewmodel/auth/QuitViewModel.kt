package com.no5ing.bbibbi.presentation.viewmodel.auth

import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.OperationStatus
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class QuitViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val sessionModule: SessionModule,
) : BaseViewModel<OperationStatus>() {
    override fun initState(): OperationStatus {
        return OperationStatus.IDLE
    }

    override fun invoke(arguments: Arguments) {
        setState(OperationStatus.RUNNING)
        withMutexScope(Dispatchers.IO) {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            restAPI.getMemberApi().deleteFcmToken(fcmToken)
            restAPI.getMemberApi().quitMember(sessionModule.sessionState.value.memberId)
                .suspendOnSuccess {
                    sessionModule.invalidateSession()
                    setState(OperationStatus.SUCCESS)
                }.suspendOnFailure {
                    setState(OperationStatus.ERROR)
                }
        }
    }
}