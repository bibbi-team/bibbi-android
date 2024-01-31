package com.no5ing.bbibbi.di

import android.content.Context
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.presentation.feature.uistate.common.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionModule @Inject constructor(val context: Context) {
    private val _sessionState = MutableStateFlow(SessionState(isLoggedIn = false))
    val sessionState: StateFlow<SessionState> = _sessionState

    init {
        Timber.d("New Session Module!!")
    }

    fun onRefreshToken(newTokenPair: AuthResult) {
        _sessionState.value = _sessionState.value.copy(
            isLoggedIn = true,
            _apiToken = newTokenPair,
        )
    }

    fun onLoginWithCredentials(newTokenPair: AuthResult, member: Member) {
        _sessionState.value = SessionState(
            isLoggedIn = true,
            _memberId = member.memberId,
            _familyId = member.familyId,
            _apiToken = newTokenPair,
        )
    }

    fun onLoginWithTemporaryCredentials(newTokenPair: AuthResult) {
        _sessionState.value = _sessionState.value.copy(
            isLoggedIn = true,
            _apiToken = newTokenPair,
            _memberId = "TEMPORARY",
        )
    }

    fun invalidateSession() {
        _sessionState.value = SessionState(isLoggedIn = false)
    }
}