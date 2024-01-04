package com.no5ing.bbibbi.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

@Composable
fun LifeCycleObserver(
    lifeCycle: LifecycleOwner,
    onStart: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    DisposableEffect(lifeCycle) {
        val observer = LifecycleEventObserver { _, event ->
            Timber.d("LifeCycleObserver event: $event")
            if (event == Lifecycle.Event.ON_START) {
                onStart()
            } else if (event == Lifecycle.Event.ON_STOP) {
                onStop()
            }
        }
        lifeCycle.lifecycle.addObserver(observer)
        onDispose {
            lifeCycle.lifecycle.removeObserver(observer)
        }
    }
}