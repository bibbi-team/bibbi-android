package com.no5ing.bbibbi.presentation.component.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.util.LocalMixpanelProvider

@Composable
fun CameraCaptureButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isCapturing: Boolean,
    ignoreDisabledState: Boolean = false,
) {
    val mixPanel = LocalMixpanelProvider.current
    Image(
        painter = painterResource(R.drawable.capture_button),
        contentDescription = null, // 필수 param
        modifier = modifier
            .size(80.dp)
            .clickable {
                mixPanel.track("Click_btn_camera")
                if (!isCapturing || ignoreDisabledState) {
                    onClick()
                }
            },
        alpha = if (isCapturing) 0.3f else 1.0f,
    )
}