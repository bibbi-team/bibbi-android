package com.no5ing.bbibbi.presentation.ui.common.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCardComplex(
    isRevealed: Boolean,
    isRevealable: Boolean,
    cardOffset: Float,
    backgroundColor: Color,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var offsetX by remember { mutableStateOf(0f) }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) cardOffset - offsetX else -offsetX },
    )

    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                onGloballyPositioned(it)
            }
            .let {
                if (isRevealable)
                    it
                        .offset { IntOffset((offsetX + offsetTransition).roundToInt() * -1, 0) }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                val original = Offset(offsetX, 0f)
                                val summed = original + Offset(x = dragAmount, y = 0f)
                                val newValue = Offset(x = summed.x.coerceIn(0f, cardOffset), y = 0f)
                                if (newValue.x >= 10) {
                                    onCollapse()
                                    return@detectHorizontalDragGestures

                                } else if (newValue.x <= 0) {
                                    onExpand()
                                    return@detectHorizontalDragGestures
                                }
                                if (change.positionChange() != Offset.Zero) change.consume()
                                offsetX = newValue.x
                            }
                        }
                else it
            },
    ) {
        content()
    }
}