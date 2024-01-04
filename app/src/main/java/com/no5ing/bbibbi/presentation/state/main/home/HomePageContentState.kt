package com.no5ing.bbibbi.presentation.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.no5ing.bbibbi.presentation.uistate.family.MainFeedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class HomePageContentState(
    val uiState: StateFlow<PagingData<MainFeedUiState>>,
)

@Composable
fun rememberHomePageContentState(
    uiState: StateFlow<PagingData<MainFeedUiState>> = remember { MutableStateFlow(PagingData.empty()) },
): HomePageContentState = HomePageContentState(
    uiState = uiState,
)