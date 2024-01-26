package com.no5ing.bbibbi.presentation.feature.state.landing.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class LoginPageState(
    val uiState: State<LoginStatus>,
    val isLoggingIn: MutableState<Boolean>,
)

@Composable
fun rememberLoginPageState(
    uiState: State<LoginStatus> = remember {
        mutableStateOf(LoginStatus.IDLE)
    },
    isLoggingIn: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
): LoginPageState = LoginPageState(
    uiState = uiState,
    isLoggingIn = isLoggingIn,
)