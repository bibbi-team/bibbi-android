package com.no5ing.bbibbi.presentation.feature.view.main.family

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.model.link.DeepLink
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveMeViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.FamilyInfoViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.FamilyInviteLinkViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMembersViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.LocalSessionState
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun FamilyPage(
    familyMembersViewModel: FamilyMembersViewModel = hiltViewModel(),
    retrieveMeViewModel: RetrieveMeViewModel = hiltViewModel(),
    familyInviteLinkViewModel: FamilyInviteLinkViewModel = hiltViewModel(),
    familyInfoViewModel: FamilyInfoViewModel = hiltViewModel(),
    onDispose: () -> Unit,
    onTapSetting: () -> Unit,
    onTapFamily: (Member) -> Unit,
    onTapShare: (String) -> Unit,
    onTapFamilyNameChange: () -> Unit,
) {
    val meId = LocalSessionState.current.memberId
    val familyId = LocalSessionState.current.familyId
    val meState = retrieveMeViewModel.uiState.collectAsState()
    val inviteLinkState = familyInviteLinkViewModel.uiState.collectAsState()
    val familyState by familyInfoViewModel.uiState.collectAsState()

    LaunchedEffect(inviteLinkState) {
        if (inviteLinkState.value.isIdle()) {
            familyInviteLinkViewModel.invoke(Arguments(arguments = mapOf("familyId" to familyId)))
        }
    }

    LaunchedEffect(meState) {
        if (meState.value.isIdle()) {
            retrieveMeViewModel.invoke(Arguments())
        }
    }
    LaunchedEffect(Unit) {
        if (familyMembersViewModel.isInitialize()) {
            familyMembersViewModel.invoke(Arguments())
        }
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            FamilyPageTopBar(
                onTapSetting = onTapSetting,
                onDispose = onDispose,
            )
            FamilyPageInviteButton(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 24.dp,
                ),
                onTapShare = onTapShare,
                uiState = inviteLinkState,
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.bbibbiScheme.backgroundSecondary
            )
            FamilyPageMemberList(
                meId = meId,
                meState = meState,
                membersState = familyMembersViewModel.uiState,
                onTapProfile = onTapFamily,
                shouldShowBalloon = familyMembersViewModel.shouldShowFamilyNewIcon(),
                familyState = familyInfoViewModel.uiState,
                onTapFamilyName = {
                    familyMembersViewModel.hideShowFamilyNewIcon()
                    onTapFamilyNameChange()
                }
            )
        }
    }
}


@Preview(
    showBackground = true,
    name = "FamilyPagePreview",
    showSystemUi = true
)
@Composable
fun FamilyPagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            FamilyPageTopBar()
            FamilyPageInviteButton(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 24.dp,
                ),
                uiState = remember {
                    mutableStateOf(APIResponse.success(DeepLink.mock()))
                },
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.bbibbiScheme.backgroundSecondary
            )
            FamilyPageMemberList(
                meId = Member.unknown().memberId,
                meState = remember { mutableStateOf(APIResponse.success(Member.unknown())) },
                membersState = MutableStateFlow(PagingData.empty()),
                familyState = MutableStateFlow(APIResponse.unknownError()),
            )
        }
    }
}
