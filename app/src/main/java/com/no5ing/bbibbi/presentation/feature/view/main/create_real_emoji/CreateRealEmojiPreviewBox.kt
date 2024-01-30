package com.no5ing.bbibbi.presentation.feature.view.main.create_real_emoji

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.no5ing.bbibbi.R

@Composable
fun CreateRealEmojiPreviewBox(
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
        Canvas(
            modifier = Modifier
                .aspectRatio(1.0f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(48.dp))
        ) {
            val circlePath = Path().apply {
                addOval(Rect(center, size.minDimension / 2))
            }
            clipPath(circlePath, clipOp = ClipOp.Difference) {
                drawRect(SolidColor(Color.Black.copy(alpha = 0.3f)))
            }
        }
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