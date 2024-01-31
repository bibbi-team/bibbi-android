package com.no5ing.bbibbi.presentation.feature.state.landing.join_family_with_link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
data class JoinFamilyWithLinkPageState(
    val ctaButtonEnabledState: MutableState<Boolean>,
    val nicknameTextState: MutableState<String>,
    val isInvalidInputState: MutableState<Boolean>,
)

@Composable
fun rememberJoinFamilyWithLinkPageState(
    ctaButtonEnabledState: MutableState<Boolean> = remember { mutableStateOf(false) },
    nicknameTextState: MutableState<String> = remember { mutableStateOf("") },
    isInvalidInputState: MutableState<Boolean> = remember { mutableStateOf(false) },
): JoinFamilyWithLinkPageState = remember {
    JoinFamilyWithLinkPageState(
        ctaButtonEnabledState = ctaButtonEnabledState,
        nicknameTextState = nicknameTextState,
        isInvalidInputState = isInvalidInputState,
    )
}