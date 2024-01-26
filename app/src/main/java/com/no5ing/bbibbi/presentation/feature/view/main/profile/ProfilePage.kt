package com.no5ing.bbibbi.presentation.feature.view.main.profile

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.feature.view_model.members.ChangeProfileImageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMemberViewModel
import com.no5ing.bbibbi.util.LocalSessionState

@Composable
fun ProfilePage(
    memberId: String,
    onDispose: () -> Unit = {},
    onTapSetting: () -> Unit = {},
    onTapPost: (Post) -> Unit = {},
    onTapChangeNickname: () -> Unit = {},
    onTapCamera: () -> Unit = {},
    changeableUriState: MutableState<Uri?> = remember { mutableStateOf(null) },
    familyMemberViewModel: FamilyMemberViewModel = hiltViewModel(),
    profileImageChangeViewModel: ChangeProfileImageViewModel = hiltViewModel(),
    memberState: State<APIResponse<Member>> = familyMemberViewModel.uiState.collectAsState(),
) {
    val isMe = memberId == LocalSessionState.current.memberId
    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.profile_title),
                rightButton = {
                    if (isMe) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clickable { onTapSetting() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.setting_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp),
                                tint = MaterialTheme.bbibbiScheme.icon
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .width(52.dp)
                        )
                    }
                }
            )
            ProfilePageMemberBar(
                memberId = memberId,
                onTapChangeNickname = onTapChangeNickname,
                onTapCamera = onTapCamera,
                changeableUriState = changeableUriState,
                familyMemberViewModel = familyMemberViewModel,
                profileImageChangeViewModel = profileImageChangeViewModel,
                memberState = memberState,
            )
            ProfilePageContent(
                memberId = memberId,
                onTapContent = onTapPost,
            )
        }
    }
}