package com.no5ing.bbibbi.presentation.feature.view.main.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarInfo
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view.common.AlbumCameraSelectDialog
import com.no5ing.bbibbi.presentation.feature.view_model.members.ChangeProfileImageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMemberViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.FamilyPostsViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ProfilePage(
    memberId: String,
    onDispose: () -> Unit = {},
    onTapSetting: () -> Unit = {},
    onTapPost: (Post) -> Unit = {},
    onTapChangeNickname: () -> Unit = {},
    onTapCamera: () -> Unit = {},
    onTapProfileImage: (String) -> Unit = {},
    changeableUriState: MutableState<Uri?> = remember { mutableStateOf(null) },
    familyMemberViewModel: FamilyMemberViewModel = hiltViewModel(),
    profileImageChangeViewModel: ChangeProfileImageViewModel = hiltViewModel(),
    familyPostsViewModel: FamilyPostsViewModel = hiltViewModel(),
    memberState: State<APIResponse<Member>> = familyMemberViewModel.uiState.collectAsState(),
) {
    val sessionState = LocalSessionState.current
    val viewerMemberId = sessionState.memberId
    val isMe = memberId == viewerMemberId
    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current
    LaunchedEffect(Unit) {
        familyPostsViewModel.invoke(Arguments(arguments = mapOf("memberId" to memberId)))
        familyMemberViewModel.invoke(Arguments(resourceId = memberId))
    }
    LaunchedEffect(changeableUriState.value) {
        if (changeableUriState.value != null) {
            profileImageChangeViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "imageUri" to changeableUriState.value.toString(),
                        "memberId" to sessionState.memberId,
                    ),
                )
            )
            changeableUriState.value = null
        }
    }
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

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                changeableUriState.value = uri
        }
    )
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
    BBiBBiSurface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfilePageTopBar(
                isMe = isMe,
                onDispose = onDispose,
                onTapSetting = onTapSetting,
            )
            ProfilePageMemberBar(
                viewerMemberId = viewerMemberId,
                onTapChangeNickname = onTapChangeNickname,
                memberState = memberState,
                onTapProfileImage = onTapProfileImage,
                onTapChangeProfileButton = {
                    albumCameraSelectState.value = true
                }
            )
            ProfilePageContent(
                onTapContent = onTapPost,
                postItemsState = familyPostsViewModel.uiState,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "ProfilePagePreview",
    showSystemUi = true
)
@Composable
fun ProfilePagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfilePageTopBar(
                isMe = false,
            )
            ProfilePageMemberBar(
                viewerMemberId = Member.unknown().memberId,
                memberState = remember { mutableStateOf(APIResponse.success(Member.unknown())) },
            )
            ProfilePageContent(
                onTapContent = {},
                postItemsState = MutableStateFlow(PagingData.empty())
            )
        }
    }
}
