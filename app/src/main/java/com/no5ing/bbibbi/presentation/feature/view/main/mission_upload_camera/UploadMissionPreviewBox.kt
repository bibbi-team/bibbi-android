package com.no5ing.bbibbi.presentation.feature.view.main.mission_upload_camera

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.no5ing.bbibbi.R

@Composable
fun UploadMissionPreviewBox(
    viewFactory: () -> PreviewView,
    onTapZoom: () -> Unit = {},
) {
    Box {
        AndroidView(
            { viewFactory() },
            modifier = Modifier
                .aspectRatio(1.0f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(48.dp)),
        )
        Box(
            modifier = Modifier
                .aspectRatio(1.0f)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.zoom_button),
                contentDescription = null,
                modifier = Modifier
                    .size(43.dp)
                    .clickable {
                        onTapZoom()
                    }
            )

        }
    }
}