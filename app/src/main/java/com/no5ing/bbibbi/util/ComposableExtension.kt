package com.no5ing.bbibbi.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.lang.Math.abs

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

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) {
    this@pxToDp.toDp()
}

@Composable
fun Modifier.verticalScrollDisabled() =
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                    val offset = it.positionChange()
                    if (abs(offset.y) > 0f) {
                        it.consume()
                    }
                }
            }
        }
    }

@Composable
fun Modifier.vix() =
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                    val offset = it.positionChange()
                    if (abs(offset.y) > 0f) {
                        it.consume()
                    }
                }
            }
        }
    }

fun Color.Companion.fromHex(colorString: String) =
    Color(android.graphics.Color.parseColor(colorString))