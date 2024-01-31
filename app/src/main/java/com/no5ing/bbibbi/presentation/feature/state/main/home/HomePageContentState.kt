package com.no5ing.bbibbi.presentation.feature.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class HomePageContentState(
    val uiState: StateFlow<PagingData<MainFeedUiState>>,
    val isInitial: MutableState<Boolean>,
)

@Composable
fun rememberHomePageContentState(
    uiState: StateFlow<PagingData<MainFeedUiState>> = remember { MutableStateFlow(PagingData.empty()) },
    isInitial: MutableState<Boolean> = remember { mutableStateOf(true) },
): HomePageContentState = HomePageContentState(
    uiState = uiState,
    isInitial = isInitial,
)