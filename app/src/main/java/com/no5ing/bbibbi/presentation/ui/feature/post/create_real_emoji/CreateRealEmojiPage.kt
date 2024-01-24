package com.no5ing.bbibbi.presentation.ui.feature.post.create_real_emoji

import android.util.Rational
import android.view.Surface.ROTATION_0
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.button.CameraCaptureButton
import com.no5ing.bbibbi.presentation.ui.common.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.ui.common.component.ClosableTopBar
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.post.MemberRealEmojiListViewModel
import com.no5ing.bbibbi.presentation.viewmodel.post.UpdateMemberPostRealEmojiViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.asyncImagePainter
import com.no5ing.bbibbi.util.emojiList
import com.no5ing.bbibbi.util.getCameraProvider
import com.no5ing.bbibbi.util.getEmojiResource
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.getRealEmojiResource
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.takePhoto
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateRealEmojiPage(
    initialEmoji: String,
    updateMemberPostRealEmojiViewModel: UpdateMemberPostRealEmojiViewModel = hiltViewModel(),
    memberRealEmojiListViewModel: MemberRealEmojiListViewModel = hiltViewModel(),
    onDispose: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHostState.current

    val uploadState by updateMemberPostRealEmojiViewModel.uiState.collectAsState()
    val emojiMap by memberRealEmojiListViewModel.uiState.collectAsState()
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    val coroutineScope = rememberCoroutineScope()
    val torchState = remember { mutableStateOf(false) }
    val isPermissionGranted = remember { mutableStateOf(false) }
    val cameraDirection = remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    val cameraState = remember { mutableStateOf<Camera?>(null) }
    val captureState = remember { mutableStateOf(ImageCapture.Builder().build()) }
    var isCapturing by remember { mutableStateOf(false) }
    val previewView = remember {
        PreviewView(
            context,
        ).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FIT_CENTER
            scaleX = -1f
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
    BBiBBiSurface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClosableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.real_emoji_upload_title),
            )
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = getEmojiResource(emojiName = selectedEmoji),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.FillBounds,
                )
                Text(
                    text = stringResource(id = R.string.real_emoji_follow_emoji),
                    color = MaterialTheme.bbibbiScheme.emojiYellow,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                AndroidView(
                    { previewView },
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp)),
                )
                Canvas(
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp))
                ) {
                    val circlePath = androidx.compose.ui.graphics.Path().apply {
                        addOval(Rect(center, size.minDimension / 2))
                    }
                    clipPath(circlePath, clipOp = ClipOp.Difference) {
                        drawRect(SolidColor(Color.Black.copy(alpha = 0.3f)))
                    }
                    drawCircle(
                        color = MaterialTheme.bbibbiScheme.emojiYellow,
                        center = center,
                        radius = (size.minDimension / 2) - 4.dp.toPx(),
                        style = Stroke(
                            4.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
                        )
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
                        coroutineScope.launch {
                            isCapturing = true
                            val uri = captureState.value.takePhoto(context)
                            isCapturing = false
                            updateMemberPostRealEmojiViewModel.invoke(
                                Arguments(
                                    arguments = mapOf(
                                        "emojiType" to selectedEmoji,
                                        "imageUri" to uri?.toString(),
                                        "prevEmojiKey" to emojiMap[selectedEmoji]?.realEmojiId,
                                    )
                                )
                            )
                        }
                    },
                    isCapturing = isCapturing || !uploadState.isIdle(),
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
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.bbibbiScheme.backgroundSecondary,
                        shape = RoundedCornerShape(1000.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                emojiList.forEach { emojiType ->
                    if (emojiMap.containsKey(emojiType)) {
                        val realEmoji = emojiMap[emojiType]!!
                        Box(
                            modifier = Modifier.clickable {
                                selectedEmoji = emojiType
                            },
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Box {
                                AsyncImage(
                                    model = asyncImagePainter(source = realEmoji.imageUrl),
                                    contentDescription = null, // 필수 param
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape),
                                )
                            }
                            Box(
                                modifier = Modifier.offset(x = 4.dp, y = 4.dp)
                            ) {
                                Image(
                                    painter = getRealEmojiResource(emojiName = emojiType),
                                    contentDescription = null, // 필수 param
                                    modifier = Modifier
                                        .size(20.dp),
                                )
                            }
                        }
                    } else {
                        Image(
                            painter = getEmojiResource(emojiName = emojiType),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(42.dp)
                                .border(
                                    width = if (selectedEmoji == emojiType) 1.5.dp else 0.dp,
                                    color = MaterialTheme.bbibbiScheme.emojiYellow,
                                    CircleShape
                                )
                                .clickable {
                                    selectedEmoji = emojiType
                                }
                        )
                    }
                }
            }
        }

    }

}

