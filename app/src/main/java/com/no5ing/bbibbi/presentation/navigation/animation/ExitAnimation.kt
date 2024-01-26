package com.no5ing.bbibbi.presentation.navigation.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntOffset

inline fun <reified T> tweenSpec() = tween<T>()
fun springSpec() = spring(
    stiffness = Spring.StiffnessLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
)

@Stable
fun fullHorizontalSlideOutToRight() = slideOutHorizontally(springSpec()) {
    +it
}

@Stable
fun fullHorizontalSlideOutToLeft() = slideOutHorizontally(springSpec()) {
    -it
}

@Stable
fun fullSlideOutVertically() = slideOutVertically(springSpec()) {
    +it
}

@Stable
fun miniHorizontalSlideOutToRight() = slideOutHorizontally {
    +it / 2
}

@Stable
fun miniHorizontalSlideOutToLeft() = slideOutHorizontally {
    -it / 2
}

@Stable
fun defaultFadeOut() = fadeOut(tweenSpec())
