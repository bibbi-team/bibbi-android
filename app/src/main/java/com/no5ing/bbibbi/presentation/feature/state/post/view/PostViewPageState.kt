package com.no5ing.bbibbi.presentation.feature.state.post.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.presentation.feature.uistate.family.MainFeedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class PostViewPageState(
    val uiState: StateFlow<APIResponse<MainFeedUiState>>,
)

@Composable
fun rememberPostViewPageState(
    uiState: StateFlow<APIResponse<MainFeedUiState>> = remember {
        MutableStateFlow(
            APIResponse.loading()
        )
    },
): PostViewPageState = PostViewPageState(
    uiState = uiState,
)