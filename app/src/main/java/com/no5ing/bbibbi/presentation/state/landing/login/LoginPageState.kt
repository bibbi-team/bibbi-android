package com.no5ing.bbibbi.presentation.state.landing.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class LoginPageState(
    val uiState: State<LoginStatus>,
)

@Composable
fun rememberLoginPageState(
    uiState: State<LoginStatus> = remember {
        mutableStateOf(LoginStatus.IDLE)
    },
): LoginPageState = LoginPageState(
    uiState = uiState,
)