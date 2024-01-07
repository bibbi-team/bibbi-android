package com.no5ing.bbibbi.presentation.ui.navigation.animation

import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Stable

@Stable
fun fullHorizontalSlideOutToRight() = slideOutHorizontally {
    + it
}

@Stable
fun fullHorizontalSlideOutToLeft() = slideOutHorizontally {
    - it
}

@Stable
fun fullSlideOutVertically() = slideOutVertically {
    + it
}

@Stable
fun miniHorizontalSlideOutToRight() = slideOutHorizontally {
    + it / 2
}

@Stable
fun miniHorizontalSlideOutToLeft() = slideOutHorizontally {
    - it / 2
}