package com.no5ing.bbibbi.presentation.feature.state.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class HomePageStoryBarState(
    val uiState: StateFlow<PagingData<Member>>,
    val meState: StateFlow<APIResponse<Member>>,
    val topState: StateFlow<Map<String, Int>>
)

@Composable
fun rememberHomePageStoryBarState(
    uiState: StateFlow<PagingData<Member>> = remember { MutableStateFlow(PagingData.empty()) },
    meState: StateFlow<APIResponse<Member>> = remember { MutableStateFlow(APIResponse.idle()) },
    topState: StateFlow<Map<String, Int>> = remember { MutableStateFlow(emptyMap()) },
): HomePageStoryBarState = HomePageStoryBarState(
    uiState = uiState,
    meState = meState,
    topState = topState,
)