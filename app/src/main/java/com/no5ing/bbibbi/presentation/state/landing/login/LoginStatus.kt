package com.no5ing.bbibbi.presentation.state.landing.login

enum class LoginStatus {
    IDLE,
    REQUESTED,
    REJECTED,
    SUCCEED_TEMPORARY,
    SUCCEED_PERMANENT_NO_FAMILY,
    SUCCEED_PERMANENT_HAS_FAMILY,
}

enum class LoginSucceedResult {
    TEMPORARY,
    PERMANENT_HAS_FAMILY,
    PERMANENT_NO_FAMILY,
}