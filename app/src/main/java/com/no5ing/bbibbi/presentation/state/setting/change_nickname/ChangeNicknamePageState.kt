package com.no5ing.bbibbi.presentation.state.setting.change_nickname

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
data class ChangeNicknamePageState(
    val ctaButtonEnabledState: MutableState<Boolean>,
    val nicknameTextState: MutableState<String>,
    val isInvalidInputState: MutableState<Boolean>,
    val invalidInputDescState: MutableState<String>,
)

@Composable
fun rememberChangeNicknamePageState(
    ctaButtonEnabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    nicknameTextState: MutableState<String> = remember { mutableStateOf("") },
    isInvalidInputState: MutableState<Boolean> = remember { mutableStateOf(false) },
    invalidInputDescState: MutableState<String> = remember { mutableStateOf("") },
): ChangeNicknamePageState = remember {
    ChangeNicknamePageState(
        ctaButtonEnabledState = ctaButtonEnabledState,
        nicknameTextState = nicknameTextState,
        isInvalidInputState = isInvalidInputState,
        invalidInputDescState = invalidInputDescState,
    )
}