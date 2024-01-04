package com.no5ing.bbibbi.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarInfo
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class BackPress {
    object Idle : BackPress()
    object InitialTouch : BackPress()
}

@Composable
fun BackToExitHandler() {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHost = LocalSnackbarHostState.current
    var backPressState by remember { mutableStateOf<BackPress>(BackPress.Idle) }
    val backMessage = stringResource(id = R.string.back_to_exit_message)

    LaunchedEffect(key1 = backPressState) {
        if (backPressState == BackPress.InitialTouch) {
            delay(2000)
            backPressState = BackPress.Idle
        }
    }


    BackHandler(backPressState == BackPress.Idle) {
        backPressState = BackPress.InitialTouch
        coroutineScope.launch {
            snackBarHost.showSnackBarWithDismiss(
                message = backMessage,
                actionLabel = snackBarInfo,
            )
        }
    }
}