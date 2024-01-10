package com.no5ing.bbibbi.presentation.ui.common.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R

@Composable
fun CameraCaptureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isCapturing: Boolean,
) {
    Image(
        painter = painterResource(R.drawable.capture_button),
        contentDescription = null, // 필수 param
        modifier = modifier
            .size(80.dp)
            .clickable {
                if(!isCapturing) onClick()
            },
        alpha = if (isCapturing) 0.3f else 1.0f,
    )
}