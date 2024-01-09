package com.no5ing.bbibbi.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleMedium = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 49.6.sp,
        letterSpacing = - (0.3.sp)
    ),
    headlineLarge = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 33.sp,
        letterSpacing = - (0.3.sp)
    ),
    headlineMedium = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 25.sp,
        letterSpacing = - (0.3.sp)
    ),
    headlineSmall = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 27.sp,
        letterSpacing = - (0.3.sp)
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = - (0.3.sp)
    ),
    bodyMedium = TextStyle(
        fontFamily = pretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

)