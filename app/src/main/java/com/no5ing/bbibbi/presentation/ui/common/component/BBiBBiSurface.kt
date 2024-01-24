package com.no5ing.bbibbi.presentation.ui.common.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme

@Composable
fun BBiBBiSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.bbibbiScheme.backgroundPrimary,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        modifier = modifier,
        content = content,
    )
}