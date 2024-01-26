package com.no5ing.bbibbi.presentation.feature.state.landing.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember

data class OnBoardingPageState @OptIn(ExperimentalFoundationApi::class) constructor(
    val currentPageState: MutableState<Int>,
    val pagerState: PagerState,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberOnBoardingPageState(
    currentPageState: MutableState<Int> = remember {
        mutableIntStateOf(0)
    },
    pagerState: PagerState = rememberPagerState { 3 }
): OnBoardingPageState = OnBoardingPageState(
    currentPageState = currentPageState,
    pagerState = pagerState,
)