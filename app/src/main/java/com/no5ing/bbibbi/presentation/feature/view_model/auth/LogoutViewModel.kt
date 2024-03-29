package com.no5ing.bbibbi.presentation.feature.view_model.auth

import com.google.firebase.messaging.FirebaseMessaging
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.OperationStatus
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.di.SessionModule
import com.no5ing.bbibbi.presentation.feature.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
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
            sessionModule.invalidateSession()
            setState(OperationStatus.SUCCESS)
        }
    }
}