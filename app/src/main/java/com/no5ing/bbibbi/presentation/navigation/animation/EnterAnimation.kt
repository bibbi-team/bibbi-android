package com.no5ing.bbibbi.presentation.navigation.animation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Stable

@Stable
fun fullHorizontalSlideInToRight() = slideInHorizontally(springSpec()) {
    -it
}

@Stable
fun fullHorizontalSlideInToLeft() = slideInHorizontally(springSpec()) {
    +it
}

@Stable
fun fullSlideInVertically() = slideInVertically(springSpec()) {
    +it
}

@Stable
fun miniHorizontalSlideInToRight() = slideInHorizontally(
    initialOffsetX = { -it / 2 },
)

@Stable
fun defaultFadeIn() = fadeIn(tweenSpec())