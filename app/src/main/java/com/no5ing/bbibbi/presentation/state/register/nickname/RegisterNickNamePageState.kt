package com.no5ing.bbibbi.presentation.state.register.nickname

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
data class RegisterNickNamePageState(
    val ctaButtonEnabledState: MutableState<Boolean>,
    val nicknameTextState: MutableState<String>,
    val isInvalidInputState: MutableState<Boolean>,
    val invalidInputDescState: MutableState<String>,
)

@Composable
fun rememberRegisterNickNamePageState(
    ctaButtonEnabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    nicknameTextState: MutableState<String> = remember { mutableStateOf("") },
    isInvalidInputState: MutableState<Boolean> = remember { mutableStateOf(false) },
    invalidInputDescState: MutableState<String> = remember { mutableStateOf("") },
): RegisterNickNamePageState = remember {
    RegisterNickNamePageState(
        ctaButtonEnabledState = ctaButtonEnabledState,
        nicknameTextState = nicknameTextState,
        isInvalidInputState = isInvalidInputState,
        invalidInputDescState = invalidInputDescState,
    )
}