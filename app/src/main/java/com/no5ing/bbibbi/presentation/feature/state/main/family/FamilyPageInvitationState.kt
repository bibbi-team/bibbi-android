package com.no5ing.bbibbi.presentation.feature.state.main.family

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.link.DeepLink

@Stable
data class FamilyPageInvitationState(
    val uiState: State<APIResponse<DeepLink>>
)

@Composable
fun rememberFamilyPageInvitationState(
    uiState: State<APIResponse<DeepLink>>,
): FamilyPageInvitationState = FamilyPageInvitationState(
    uiState = uiState,
)