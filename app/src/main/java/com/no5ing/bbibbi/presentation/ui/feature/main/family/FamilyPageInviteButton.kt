package com.no5ing.bbibbi.presentation.ui.feature.main.family

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.main.family.FamilyPageInvitationState
import com.no5ing.bbibbi.presentation.state.main.family.rememberFamilyPageInvitationState
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.viewmodel.family.FamilyInviteLinkViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun FamilyPageInviteButton(
    familyInviteLinkViewModel: FamilyInviteLinkViewModel = hiltViewModel(),
    familyPageInvitationState: FamilyPageInvitationState = rememberFamilyPageInvitationState(
        uiState = familyInviteLinkViewModel.uiState.collectAsState()
    ),
    onTapShare: (String) -> Unit,
) {
    val mixPanel = LocalMixpanelProvider.current
    val familyId = LocalSessionState.current.familyId
    val uiState by familyPageInvitationState.uiState
    LaunchedEffect(uiState) {
        if (uiState.isIdle()) {
            familyInviteLinkViewModel.invoke(Arguments(arguments = mapOf("familyId" to familyId)))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.bbibbiScheme.backgroundSecondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                vertical = 22.dp,
                horizontal = 20.dp,
            )
            .clickable {
                if (uiState.isReady()) {
                    mixPanel.track("Click_ShareLink_Family")
                    onTapShare(uiState.data.url)
                }
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.invite_icon),
                    contentDescription = null, // 필수 param
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(13.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.family_copy_link),
                        color = MaterialTheme.bbibbiScheme.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = if (uiState.isReady()) uiState.data.url else "Loading...",
                        color = MaterialTheme.bbibbiScheme.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.share_icon),
                contentDescription = null,
                tint = MaterialTheme.bbibbiScheme.icon,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}