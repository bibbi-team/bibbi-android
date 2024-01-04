package com.no5ing.bbibbi.presentation.state.register.profile_image

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Stable
data class RegisterProfileImagePageState(
    val profileImageUri: MutableState<Uri?>,
    val currentStageState: MutableState<Int>,
)

@Composable
fun rememberRegisterProfileImagePageState(
    profileImageUri: MutableState<Uri?> = remember { mutableStateOf(null) },
    currentStageState: MutableState<Int> = remember { mutableStateOf(1) },
): RegisterProfileImagePageState = remember {
    RegisterProfileImagePageState(
        profileImageUri = profileImageUri,
        currentStageState = currentStageState,
    )
}