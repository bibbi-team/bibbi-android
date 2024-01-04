package com.no5ing.bbibbi.presentation.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.model.member.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class HomePageStoryBarState(
    val uiState: StateFlow<PagingData<Member>>
)

@Composable
fun rememberHomePageStoryBarState(
    uiState: StateFlow<PagingData<Member>> = remember { MutableStateFlow(PagingData.empty()) },
): HomePageStoryBarState = HomePageStoryBarState(
    uiState = uiState,
)