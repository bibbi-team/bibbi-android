package com.no5ing.bbibbi.presentation.feature.view.common

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.BannerAd
import com.no5ing.bbibbi.presentation.component.ClosableTopBar
import com.no5ing.bbibbi.presentation.component.button.CameraCaptureButton
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.util.getAdView
import com.no5ing.bbibbi.util.getCameraProvider
import com.no5ing.bbibbi.util.takePhotoWithImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    onImageCaptured: (Uri?) -> Unit = {},
    onDispose: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val coroutineScope = rememberCoroutineScope()
    val torchState = remember { mutableStateOf(false) }
    val isPermissionGranted = remember { mutableStateOf(false) }
    val cameraDirection = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
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

    val adView = getAdView()
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
    BBiBBiSurface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                ClosableTopBar(
                    onDispose = onDispose,
                    title = stringResource(id = R.string.camera_title),
                )
                Spacer(modifier = Modifier.height(48.dp))
                Box {
                    AndroidView(
                        { previewView },
                        modifier = Modifier
                            .aspectRatio(1.0f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(48.dp))
                            .background(MaterialTheme.bbibbiScheme.backgroundHover),
                    )
                    Box(
                        modifier = Modifier
                            .aspectRatio(1.0f)
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zoom_button),
                            contentDescription = null,
                            modifier = Modifier
                                .size(43.dp)
                                .clickable {
                                    isZoomed = !isZoomed
                                }
                        )

                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.toggle_flash_button),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                torchState.value = !torchState.value
                                cameraState.value?.cameraControl?.enableTorch(torchState.value)
                            }
                    )
                    CameraCaptureButton(
                        onClick = {
                            if (isCapturing) return@CameraCaptureButton
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                isCapturing = true
                                val uri = captureState.value.takePhotoWithImage(
                                    context,
                                    requiredFlip = cameraDirection.value == CameraSelector.DEFAULT_FRONT_CAMERA
                                )
                                isCapturing = false
                                onImageCaptured(uri)
                            }
                        },
                        isCapturing = isCapturing,
                    )
                    Image(
                        painter = painterResource(R.drawable.rorate_button),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                cameraDirection.value = run {
                                    if (cameraDirection.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                }

                            }
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.systemBars)) {
                BannerAd(adView = adView, modifier = Modifier.fillMaxWidth())
            }

        }

    }

}

