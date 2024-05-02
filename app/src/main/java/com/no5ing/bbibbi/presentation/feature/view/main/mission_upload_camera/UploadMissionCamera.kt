package com.no5ing.bbibbi.presentation.feature.view.main.mission_upload_camera

import android.net.Uri
import android.util.Rational
import android.view.Surface.ROTATION_0
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view_model.mission.GetTodayMissionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.MemberRealEmojiListViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.UpdateMemberPostRealEmojiViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getCameraProvider
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.takePhotoWithImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UploadMissionCamera(
    todayMissionViewModel: GetTodayMissionViewModel = hiltViewModel(),
    updateMemberPostRealEmojiViewModel: UpdateMemberPostRealEmojiViewModel = hiltViewModel(),
    memberRealEmojiListViewModel: MemberRealEmojiListViewModel = hiltViewModel(),
    onDispose: () -> Unit = {},
    onImageCaptured: (Uri?) -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHostState.current
    val haptic = LocalHapticFeedback.current

    val uploadState by updateMemberPostRealEmojiViewModel.uiState.collectAsState()
    val emojiMap by memberRealEmojiListViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val torchState = remember { mutableStateOf(false) }
    val isPermissionGranted = remember { mutableStateOf(false) }
    val cameraDirection = remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    val cameraState = remember { mutableStateOf<Camera?>(null) }
    val captureState = remember { mutableStateOf(ImageCapture.Builder().build()) }
    var isCapturing by remember { mutableStateOf(false) }
    var isZoomed by remember { mutableStateOf(false) }
    val previewView = remember {
        PreviewView(
            context,
        ).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FIT_CENTER
        }
    }

    LaunchedEffect(Unit) {
        if (todayMissionViewModel.uiState.value.isIdle()) {
            todayMissionViewModel.invoke(Arguments())
        }
    }

    val resources = localResources()
    LaunchedEffect(uploadState) {
        when (uploadState.status) {
            is APIResponse.Status.SUCCESS -> {
                updateMemberPostRealEmojiViewModel.resetState()
                memberRealEmojiListViewModel.invoke(Arguments())
            }

            is APIResponse.Status.ERROR -> {
                snackBarHost.showSnackBarWithDismiss(
                    message = resources.getErrorMessage(errorCode = uploadState.errorCode),
                    actionLabel = snackBarWarning
                )
                updateMemberPostRealEmojiViewModel.resetState()
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        memberRealEmojiListViewModel.invoke(Arguments())
    }

    LaunchedEffect(cameraDirection.value, isPermissionGranted.value) {
        if (!isPermissionGranted.value) return@LaunchedEffect
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        val preview = Preview
            .Builder()
            .build()
        val capturer = ImageCapture
            .Builder()
            .build()

        captureState.value = capturer

        val useCaseGroup = UseCaseGroup
            .Builder()
            .addUseCase(capturer)
            .addUseCase(preview)
            .setViewPort(
                ViewPort.Builder(
                    Rational(
                        previewView.width,
                        previewView.height
                    ),
                    ROTATION_0
                ).build()
            )
            .build()
        cameraState.value = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraDirection.value,
            useCaseGroup
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    val perm =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA) { isGranted ->
            if (!isGranted) {
                onDispose()
            } else {
                isPermissionGranted.value = true
            }
        }
    LaunchedEffect(Unit) {
        if (perm.status.isGranted) {
            isPermissionGranted.value = true
        } else {
            perm.launchPermissionRequest()
        }
    }
    val zoomValue: Float by animateFloatAsState(
        targetValue = if (isZoomed) 0.5f else 0.0f,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing,
        ), label = ""
    )
    LaunchedEffect(zoomValue) {
        cameraState.value?.cameraControl?.setLinearZoom(
            zoomValue
        )
    }
    val missionModel by todayMissionViewModel.uiState.collectAsState()
    BBiBBiSurface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UploadMissionTopBar(
                onDispose = onDispose,
            )
            Spacer(modifier = Modifier.height(48.dp))
            UploadMissionDisplayBar(
                missionText = if (missionModel.isReady()) missionModel.data.content else ""
            )
            Spacer(modifier = Modifier.height(16.dp))
            UploadMissionPreviewBox(
                viewFactory = { previewView },
                onTapZoom = {
                    isZoomed = !isZoomed
                }
            )
            Spacer(modifier = Modifier.height(36.dp))
            UploadMissionCameraBar(
                isCapturing = isCapturing || !uploadState.isIdle(),
                onClickTorch = {
                    torchState.value = !torchState.value
                    cameraState.value?.cameraControl?.enableTorch(torchState.value)
                },
                onClickCapture = {
                    if (isCapturing) return@UploadMissionCameraBar
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    coroutineScope.launch {
                        isCapturing = true
                        val image = captureState.value.takePhotoWithImage(
                            context,
                            requiredFlip = cameraDirection.value == CameraSelector.DEFAULT_FRONT_CAMERA
                        )
                        isCapturing = false
                        onImageCaptured(image)
                    }
                },
                onClickRotate = {
                    cameraDirection.value = run {
                        if (cameraDirection.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(36.dp))
        }

    }

}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "CreateRealEmojiPagePreview",
    showSystemUi = true
)
@Composable
fun CreateRealEmojiPagePreview() {
    val context = LocalContext.current
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UploadMissionTopBar()
                Spacer(modifier = Modifier.height(48.dp))
                UploadMissionDisplayBar(
                    missionText = "오늘 입고 간 옷이 잘 나오도록 찍어주세요"
                )
                Spacer(modifier = Modifier.height(16.dp))
                UploadMissionPreviewBox(
                    viewFactory = { PreviewView(context) },
                )
                Spacer(modifier = Modifier.height(36.dp))
                UploadMissionCameraBar(
                    isCapturing = false,
                )
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
