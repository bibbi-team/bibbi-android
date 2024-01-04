package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.no5ing.bbibbi.util.getScreenSize

@Composable
fun OuterClickListener(onClick: () -> Unit) {
    val (width, height) = getScreenSize()
    Box(
        modifier = Modifier
            .requiredSize(width, height)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onClick()
                }
            }
    )
}