package com.no5ing.bbibbi.presentation.ui.feature.main.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R

@Composable
fun BoxScope.HomePageUploadButton(
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(vertical = 15.dp)
            .systemBarsPadding()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UploadHelperPop()
            UploadButton(
                onTap = onTap
            )
        }
    }
}


@Composable
fun UploadButton(
    onTap: () -> Unit,
) {
    Image(
        painter = painterResource(R.drawable.capture_button),
        contentDescription = null, // 필수 param
        modifier = Modifier
            .size(80.dp)
            .clickable { onTap() }
    )
}

@Composable
fun UploadHelperPop() {
    Box(modifier = Modifier.padding(bottom = 30.dp)) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(6.dp))
                .padding(
                    vertical = 10.dp,
                    horizontal = 14.dp
                )
        ) {
            Text(
                text = stringResource(id = R.string.home_one_image_per_day),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 24.dp)
                .size(24.dp, 24.dp)
        ) {
            val surfaceColor = MaterialTheme.colorScheme.surface
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val rect = Rect(Offset.Zero, size)
                val trianglePath = Path().apply {
                    moveTo(rect.topLeft.x, rect.topLeft.y)
                    lineTo(rect.topRight.x, rect.topRight.y)
                    lineTo(rect.bottomCenter.x, rect.bottomCenter.y)
                    close()
                }

                drawIntoCanvas { canvas ->
                    canvas.drawOutline(
                        outline = Outline.Generic(trianglePath),
                        paint = Paint().apply {
                            color = surfaceColor
                            pathEffect = PathEffect.cornerPathEffect(rect.maxDimension / 5)
                        }
                    )
                }
            }
        }
    }
}