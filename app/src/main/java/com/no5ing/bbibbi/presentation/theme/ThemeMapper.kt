package com.no5ing.bbibbi.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MaterialTheme.bbibbiScheme: BbiBbiColorScheme
    get() = BbiBbiColorScheme

object BbiBbiColorScheme {
    val backgroundPrimary: Color
        @Composable get() = MaterialTheme.colorScheme.background
    val backgroundSecondary: Color
        @Composable get() = MaterialTheme.colorScheme.onBackground
    val backgroundHover: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceTint
    val button: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    val iconSelected: Color
        @Composable get() = MaterialTheme.colorScheme.primary
    val textPrimary: Color
        @Composable get() = MaterialTheme.colorScheme.secondary
    val textSecondary: Color
        @Composable get() = MaterialTheme.colorScheme.tertiary
    val icon: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface
    val white: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary

    val gray600: Color
        @Composable get() = MaterialTheme.colorScheme.onSecondary

    val gray500: Color
        @Composable get() = MaterialTheme.colorScheme.onTertiary

    /**
     *
     * Main Color
     */
    val mainYellow = Color(0xFFF5F378)
    val mainGreenHover = Color(0xFF3FD960)

    /**
     * Graphic Color
     */
    val graphicPink = Color(0xFFEE92EE)
    val graphicPurple = Color(0xFFCAA8FF)
    val graphicBlue = Color(0xFF658FFF)
    val emojiYellow = Color(0xFFCAED64)
    val warningRed = Color(0xFFEB546F)

    /**
     * Unconfined
     */
    val kakaoYellow = Color(0xFFFEE500)
    val criticalRed = Color(0xFFFF4015)
}