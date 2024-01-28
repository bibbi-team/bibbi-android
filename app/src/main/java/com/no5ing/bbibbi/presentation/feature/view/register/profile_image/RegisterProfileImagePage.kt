package com.no5ing.bbibbi.presentation.feature.view.register.profile_image


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.feature.state.register.profile_image.RegisterProfileImagePageState
import com.no5ing.bbibbi.presentation.feature.state.register.profile_image.rememberRegisterProfileImagePageState
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.feature.view.common.AlbumCameraSelectDialog
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RegisterMemberViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources

@Composable
fun RegisterProfileImagePage(
    nickName: String,
    dayOfBirth: String,
    state: RegisterProfileImagePageState = rememberRegisterProfileImagePageState(),
    onNextPage: () -> Unit,
    onTapCamera: () -> Unit,
    onDispose: () -> Unit,
    registerMemberViewModel: RegisterMemberViewModel = hiltViewModel(),
) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null)
                state.profileImageUri.value = uri
        }
    )
    val registerState = registerMemberViewModel.uiState.collectAsState()
    val snackBarHost = LocalSnackbarHostState.current
    val resources = localResources()
    LaunchedEffect(registerState.value) {
        val currentState = registerState.value
        when (registerState.value.status) {
            is APIResponse.Status.ERROR -> {
                val errorCode = currentState.errorCode
                val errorMessage = resources.getErrorMessage(errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errorMessage,
                    actionLabel = snackBarWarning,
                )
            }

            is APIResponse.Status.SUCCESS -> {
                onNextPage()
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
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DisposableTopBar(onDispose = onDispose, title = "")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.register_profile_image_description,
                        nickName
                    ),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Box(
                        modifier = Modifier.clickable {
                            albumCameraSelectState.value = true
                        }
                    ) {
                        if (state.profileImageUri.value == null) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(90.dp)
                                    .background(MaterialTheme.bbibbiScheme.backgroundHover)
                            )
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                Text(
                                    text = "${nickName.first()}",
                                    fontSize = 28.sp,
                                    color = MaterialTheme.bbibbiScheme.white,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }

                        } else {
                            Image(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(90.dp)
                                    .background(MaterialTheme.bbibbiScheme.mainYellow),
                                painter = rememberAsyncImagePainter(model = state.profileImageUri.value),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CTAButton(
                    text = stringResource(id = R.string.register_complete),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    onClick = {
                        registerMemberViewModel.invoke(
                            Arguments(
                                arguments = mapOf(
                                    "imageUri" to state.profileImageUri.value?.toString(),
                                    "memberName" to nickName,
                                    "dayOfBirth" to dayOfBirth,
                                )
                            )
                        )

                    },
                    isActive = registerState.value.isIdle()
                )
            }

        }
    }
}