package com.no5ing.bbibbi.presentation.viewmodel.auth

import androidx.lifecycle.viewModelScope
import com.no5ing.bbibbi.data.datasource.local.LocalDataStorage
import com.no5ing.bbibbi.data.datasource.network.RestAPI
import com.no5ing.bbibbi.data.model.auth.SocialLoginRequest
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.landing.login.LoginStatus
import com.no5ing.bbibbi.presentation.viewmodel.BaseViewModel
import com.skydoves.sandwich.retrofit.body
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginWithCredentialsViewModel @Inject constructor(
    private val restAPI: RestAPI,
    private val localDataStorage: LocalDataStorage,
) : BaseViewModel<LoginStatus>() {
    override fun initState(): LoginStatus {
        return LoginStatus.IDLE
    }

    override fun invoke(arguments: Arguments) {
        setState(LoginStatus.REQUESTED)
        val authKey = arguments.get("authKey") ?: throw RuntimeException()
        val provider = arguments.get("provider") ?: throw RuntimeException()
        withMutexScope(Dispatchers.IO) {
            val authResult = if(provider == "kakao") restAPI.getAuthApi().kakaoLogin(
                SocialLoginRequest(
                    accessToken = authKey,
                )
            ) else restAPI.getAuthApi().googleLogin(
                SocialLoginRequest(
                    accessToken = authKey,
                )
            )
            authResult.suspendOnSuccess {
                val authToken = body
                localDataStorage.setAuthTokens(authToken)
                if (body.isTemporaryToken) {
                    // 임시토큰이니까.. 회원가입 플로우로 넘겨야함
                    setState(LoginStatus.SUCCEED_TEMPORARY)
                } else {
                    val meResult = restAPI.getMemberApi().getMeInfo()
                    meResult.suspendOnSuccess {
                        localDataStorage.login(data, authToken)
                        if (body.hasFamily()) {
                            setState(LoginStatus.SUCCEED_PERMANENT_HAS_FAMILY)
                        } else {
                            setState(LoginStatus.SUCCEED_PERMANENT_NO_FAMILY)
                        }
                    }.suspendOnFailure {
                        setState(LoginStatus.REJECTED)
                    }
                }
            }.suspendOnFailure {
                setState(LoginStatus.REJECTED)
            }
        }
    }


}