package com.no5ing.bbibbi.presentation.feature.uistate.common

import com.no5ing.bbibbi.data.model.auth.AuthResult

data class SessionState(
    private val isLoggedIn: Boolean,
    private val _memberId: String? = null,
    private val _familyId: String? = null,
    private val _apiToken: AuthResult? = null,
) {
    /**
     * LoggedIn이면 무조건 존재해야하는 얘들
     */
    val memberId: String
        get() = _memberId ?: throw IllegalStateException("memberId is null")
    val apiToken: AuthResult
        get() = _apiToken ?: throw IllegalStateException("accessToken is null")

    val familyId: String
        get() = _familyId ?: throw IllegalStateException("familyId is null") //TODO 얘는 널일수 있음

    fun hasFamily() = _familyId != null


    fun isLoggedIn() = isLoggedIn
}
