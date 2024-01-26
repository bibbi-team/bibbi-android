package com.no5ing.bbibbi.presentation.component.button

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun CTAButton(
    text: String,
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.bbibbiScheme.mainYellow,
    textColor: Color = MaterialTheme.bbibbiScheme.backgroundPrimary,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit = {},
    isActive: Boolean = true,
    byPassCtaIgnore: Boolean = false,
) {
    val opacityAlpha: Float by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.2f,
        animationSpec = tween(
            durationMillis = 130,
            easing = LinearEasing,
        ), label = ""
    )
    Button(
        shape = RoundedCornerShape(100.dp),
        onClick = { if (isActive || byPassCtaIgnore) onClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor.copy(
                alpha = opacityAlpha
            )
        ),
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.bbibbiTypo.bodyOneBold,
        )
    }
}