package com.no5ing.bbibbi.presentation.ui.feature.dialog

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.ui.common.component.ClosableTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraDialog(
    onImageCaptured: (Uri?) -> Unit = {},
    onDispose: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDispose
    ) {
        val torchState = remember { mutableStateOf(false) }
        val cameraSurfaceState = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

        val cameraState = remember { mutableStateOf<Camera?>(null) }
        val captureState = remember { mutableStateOf(ImageCapture.Builder().build()) }

        val preview = Preview.Builder().build()
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val previewView = remember { PreviewView(context) }
        var internalCameraProvider: ProcessCameraProvider? = null

        val bindCamera = suspend {
            Log.d("Bind!!", "Bind!!")
            val cameraProvider = context.getCameraProvider()
            internalCameraProvider = cameraProvider
            cameraProvider.unbindAll()
            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(captureState.value)
                .setViewPort(
                    ViewPort.Builder(
                        Rational(
                            previewView.width,
                            previewView.height
                        ),
                        previewView.display.rotation
                    )
                        .build()
                )
                .build()
            cameraState.value = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSurfaceState.value,
                useCaseGroup
            )

            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
        val perm =
            rememberPermissionState(permission = android.Manifest.permission.CAMERA) { isGranted ->
                if (!isGranted) {
                    Timber.d("Not Accepted!!")
                    onDispose()
                } else {
                    coroutineScope.launch {
                        bindCamera()
                    }
                }
            }
        LaunchedEffect(Unit) {
            if (perm.status.isGranted) {
                bindCamera()
            } else {
                perm.launchPermissionRequest()
            }

        }
        DisposableEffect(internalCameraProvider) {
            onDispose {
                internalCameraProvider?.unbindAll()
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                ClosableTopBar(
                    onDispose = onDispose,
                    title = stringResource(id = R.string.camera_title),
                )
                Spacer(modifier = Modifier.height(48.dp))
                AndroidView(
                    { previewView },
                    modifier = Modifier
                        .aspectRatio(1.0f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48.dp)),
                )
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
                    Image(
                        painter = painterResource(R.drawable.capture_button),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                coroutineScope.launch {
                                    val uri = captureState.value.takePhoto(context)
                                    onImageCaptured(uri)
                                }
                            }
                    )
                    Image(
                        painter = painterResource(R.drawable.rotate_button),
                        contentDescription = null, // 필수 param
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                cameraSurfaceState.value = run {
                                    if (cameraSurfaceState.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    bindCamera()
                                }

                            }
                    )
                }
            }

        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private suspend fun ImageCapture.takePhoto(context: Context): Uri? =
    suspendCoroutine<Uri?> { continuation ->
        val name = "${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata.
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken.
        Log.d("D", "capture!!")
        this.takePicture(
            outputOptions,
            //Executors.newSingleThreadExecutor(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e("", "photo capture failed", e)
                    continuation.resume(null)
                }

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    Timber.d("onImageSaved: ${output.savedUri}")
                    continuation.resume(output.savedUri)
                }
            }
        )
    }