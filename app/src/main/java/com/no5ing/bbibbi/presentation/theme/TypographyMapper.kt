package com.no5ing.bbibbi.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

val MaterialTheme.bbibbiTypo: BbiBbiTypography
    get() = BbiBbiTypography

object BbiBbiTypography {
    val title: TextStyle
        @Composable get() = MaterialTheme.typography.titleMedium
    val titleTwo: TextStyle
        @Composable get() = MaterialTheme.typography.titleLarge

    val headOne: TextStyle
        @Composable get() = MaterialTheme.typography.headlineLarge
    val headTwoBold: TextStyle
        @Composable get() = MaterialTheme.typography.headlineMedium
    val headTwoRegular: TextStyle
        @Composable get() = MaterialTheme.typography.headlineSmall

    val bodyOneBold: TextStyle
        @Composable get() = MaterialTheme.typography.bodyLarge
    val bodyOneRegular: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium
    val bodyTwoBold: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    val bodyTwoRegular: TextStyle
        @Composable get() = MaterialTheme.typography.bodySmall

    val caption: TextStyle
        @Composable get() = MaterialTheme.typography.labelSmall
}