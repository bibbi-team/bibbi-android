package com.no5ing.bbibbi.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.no5ing.bbibbi.presentation.feature.uistate.common.SessionState

val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

val LocalNavigateControllerState =
    staticCompositionLocalOf<NavHostController> { error("No NavHostController provided") }

val LocalSessionState =
    compositionLocalOf<SessionState> { error("No SessionState provided") }

val LocalDeepLinkState =
    compositionLocalOf<String?> { error("No DeepLinkState provided") }

val LocalMixpanelProvider =
    staticCompositionLocalOf<MixpanelWrapper> { error("No MixpanelProvider provided") }