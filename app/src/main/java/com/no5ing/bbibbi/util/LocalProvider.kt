package com.no5ing.bbibbi.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

val LocalNavigateControllerState =
    compositionLocalOf<NavHostController> { error("No NavHostController provided") }