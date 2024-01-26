package com.no5ing.bbibbi.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.no5ing.bbibbi.presentation.theme.BbibbiTheme
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme

@Composable
fun BBiBBiPreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BbibbiTheme {
        Surface(
            color = MaterialTheme.bbibbiScheme.backgroundPrimary,
            modifier = modifier,
        ) {
            content()
        }
    }

}