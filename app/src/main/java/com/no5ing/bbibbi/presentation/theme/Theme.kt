package com.no5ing.bbibbi.presentation.theme

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * BackGround
 */
val Black = Color(0xFF242427) //background
val Gray900 = Color(0xff2F2F32) //onbackground
val Gray800 = Color(0xFF353538)
val Gray700 = Color(0xff3F3F43) //surface
val Gray600 = Color(0xFF515155)

/**
 * Text & Icon
 */
val Gray500 = Color(0xFF7B7B7E)
val Gray400 = Color(0xff8C8C8E) //onsurface
val Gray300 = Color(0xffB2B2B4) //tertiary
val Gray200 = Color(0xffD3D3D3) //secondary
val Gray100 = Color(0xFFE8E8E8) //primary
val White = Color(0xFFFFFFFF)


private val LightColorScheme = lightColorScheme(
    /**
     * Base Colors
     */
    onPrimary = White,
    primary = Gray100,
    secondary = Gray200,
    tertiary = Gray300,
    onSurface = Gray400,
    onTertiary = Gray500,
    onSecondary = Gray600,
    surface = Gray700,
    surfaceTint = Gray800,
    onBackground = Gray900,
    background = Black,
)

@Composable
fun BbibbiTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                )
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}