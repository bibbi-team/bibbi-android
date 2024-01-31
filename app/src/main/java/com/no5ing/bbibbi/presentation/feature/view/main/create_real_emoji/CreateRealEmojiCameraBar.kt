package com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.button.CameraCaptureButton

@Composable
fun CreateRealEmojiCameraBar(
    isCapturing: Boolean,
    onClickTorch: () -> Unit = {},
    onClickCapture: () -> Unit = {},
    onClickRotate: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.toggle_flash_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    onClickTorch()
                }
        )
        CameraCaptureButton(
            onClick = onClickCapture,
            isCapturing = isCapturing,
        )
        Image(
            painter = painterResource(R.drawable.rorate_button),
            contentDescription = null, // 필수 param
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    onClickRotate()
                }
        )
    }
}