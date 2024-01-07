package com.no5ing.bbibbi.presentation.ui.navigation.animation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Stable

@Stable
fun fullHorizontalSlideInToRight() = slideInHorizontally {
    - it
}

@Stable
fun fullHorizontalSlideInToLeft() = slideInHorizontally {
    + it
}

@Stable
fun fullSlideInVertically() = slideInVertically {
    + it
}

@Stable
fun miniHorizontalSlideInToRight() = slideInHorizontally(
    initialOffsetX = { -it / 2 },
)