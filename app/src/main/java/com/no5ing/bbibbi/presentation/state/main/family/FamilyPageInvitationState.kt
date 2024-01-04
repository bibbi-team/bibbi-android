package com.no5ing.bbibbi.presentation.state.main.family

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.no5ing.bbibbi.presentation.uistate.family.FamilyInviteLinkUiState

@Stable
data class FamilyPageInvitationState(
    val uiState: State<FamilyInviteLinkUiState>
)

@Composable
fun rememberFamilyPageInvitationState(
    uiState: State<FamilyInviteLinkUiState>,
): FamilyPageInvitationState = FamilyPageInvitationState(
    uiState = uiState,
)