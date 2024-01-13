package com.no5ing.bbibbi.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

fun Modifier.dashedBorder(width: Dp, radius: Dp, color: Color) =
    drawBehind {
        drawIntoCanvas {
            val paint = Paint()
                .apply {
                    strokeWidth = width.toPx()
                    this.color = color
                    style = PaintingStyle.Stroke
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f)
                }
            it.drawRoundRect(
                width.toPx(),
                width.toPx(),
                size.width - width.toPx(),
                size.height - width.toPx(),
                radius.toPx(),
                radius.toPx(),
                paint
            )
        }
    }