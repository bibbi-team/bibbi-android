package com.no5ing.bbibbi.presentation.ui.feature.main.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.CircleProfileImage
import com.no5ing.bbibbi.presentation.ui.feature.dialog.AlbumCameraSelectDialog
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarInfo
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.viewmodel.members.ChangeProfileImageViewModel
import com.no5ing.bbibbi.presentation.viewmodel.members.FamilyMemberViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources

@Composable
fun ProfilePageMemberBar(
    memberId: String,
    familyMemberViewModel: FamilyMemberViewModel = hiltViewModel(),
    profileImageChangeViewModel: ChangeProfileImageViewModel = hiltViewModel(),
    memberState: State<APIResponse<Member>> = familyMemberViewModel.uiState.collectAsState(),
    onTapChangeNickname: () -> Unit = {},
    onTapCamera: () -> Unit = {},
    changeableUriState: MutableState<Uri?> = remember { mutableStateOf(null) },
    isMe: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    LaunchedEffect(Unit) {
        familyMemberViewModel.invoke(Arguments(resourceId = memberId))
        isMe.value = familyMemberViewModel.me?.memberId == memberId
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                changeableUriState.value = uri
        }
    )
    LaunchedEffect(changeableUriState.value) {
        if (changeableUriState.value != null) {
            profileImageChangeViewModel.invoke(
                Arguments(
                    arguments = mapOf("imageUri" to changeableUriState.value.toString()),
                )
            )
            changeableUriState.value = null
        }
    }

    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current
    val changeImageState = profileImageChangeViewModel.uiState.collectAsState()
    LaunchedEffect(changeImageState.value) {
        when (changeImageState.value.status) {
            is APIResponse.Status.SUCCESS -> {
                snackBarHost.showSnackBarWithDismiss(
                    message = resources.getString(R.string.profile_image_changed_successfully),
                    actionLabel = snackBarInfo,
                )
                profileImageChangeViewModel.resetState()
                familyMemberViewModel.invoke(Arguments(resourceId = memberId))
            }

            is APIResponse.Status.ERROR -> {
                val errorCode = changeImageState.value.errorCode
                val errorMessage = resources.getErrorMessage(errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errorMessage,
                    actionLabel = snackBarWarning,
                )
                profileImageChangeViewModel.resetState()
            }

            else -> {}
        }
    }
    val albumCameraSelectState = remember { mutableStateOf(false) }
    AlbumCameraSelectDialog(
        enabledState = albumCameraSelectState,
        onAlbum = {
            albumCameraSelectState.value = false
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCamera = {
            albumCameraSelectState.value = false
            onTapCamera()
        },
        onCancel = {
            albumCameraSelectState.value = false
        }
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (memberState.value.isReady()) {
            Spacer(modifier = Modifier.height(20.dp))
            Box {
                Box {
                    CircleProfileImage(
                        member = memberState.value.data,
                        size = 90.dp,
                    )
                }
                if (familyMemberViewModel.me?.memberId == memberState.value.data.memberId) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable {
                                albumCameraSelectState.value = true
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.change_image_icon),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = memberState.value.data.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                if (familyMemberViewModel.me?.memberId == memberState.value.data.memberId) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.write_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onTapChangeNickname() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}