package com.no5ing.bbibbi.presentation.feature.view.main.family_studio_upload

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarCamera
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view.main.post_upload.PostUploadPageImagePreview
import com.no5ing.bbibbi.presentation.feature.view.main.post_upload.PostUploadPageTopBar
import com.no5ing.bbibbi.presentation.feature.view.main.post_upload.PostUploadPageUploadBar
import com.no5ing.bbibbi.presentation.feature.view_model.post.ConvertAIImageViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.post.CreateFamilyStudioPostViewModel
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.UUID


@Composable
fun FamilyStudioUploadPage(
    onDispose: () -> Unit,
    imageUrl: State<Uri?>,
    convertAIImageViewModel: ConvertAIImageViewModel = hiltViewModel(),
    createPostViewModel: CreateFamilyStudioPostViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHostState.current
    val snackSavedMessage = stringResource(id = R.string.snack_bar_saved)
    LaunchedEffect(Unit) {
        if (imageUrl.value == null) {
            onDispose()
        }
    }
    val mixPanel = LocalMixpanelProvider.current
    val convertResult = convertAIImageViewModel.uiState.collectAsState()
    val uploadResult = createPostViewModel.uiState.collectAsState()
    val snackErrorMessage = getErrorMessage(errorCode = uploadResult.value.errorCode)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val playLottie = convertResult.value.isLoading()
    val progress = animateLottieCompositionAsState(composition, isPlaying = playLottie, iterations = LottieConstants.IterateForever)
    LaunchedEffect(imageUrl.value) {
        if (imageUrl.value != null && convertResult.value.isIdle()) {
            convertAIImageViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "imageUri" to imageUrl.value.toString(),
                    )
                )
            )
        }
    }
    LaunchedEffect(uploadResult.value) {
        if (uploadResult.value.isReady()) {
            onDispose()
        } else if (uploadResult.value.isFailed()) {
            snackBarHost.showSnackBarWithDismiss(
                message = snackErrorMessage,
                actionLabel = snackBarWarning
            )
            createPostViewModel.resetState()
        }
    }
    BBiBBiSurface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PostUploadPageTopBar(
                        onDispose = onDispose,
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    if (convertResult.value.isReady()) {
                        ConvertedImagePreview(
                            imageUrl = convertResult.value.data.imageUrl,
                        )
                    } else {
                        PostUploadPageImagePreview(
                            previewImgUrl = imageUrl.value,
                            onTapImageTextButton = {},
                            supportText = false,
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                    PostUploadPageUploadBar(
                        isIdle = uploadResult.value.isIdle() && convertResult.value.isReady(),
                        isSaveIdle = convertResult.value.isReady(),
                        onClickUpload = {
                            mixPanel.track("Click_UploadPhoto")
                            createPostViewModel.invoke(
                                Arguments(
                                    arguments = mapOf(
                                        "imageUrl" to convertResult.value.data.imageUrl,
                                        "type" to "AI_IMAGE"
                                    )
                                )
                            )
                        },
                        onClickSave = {
                            coroutineScope.launch {
                                saveImageFromUrl(
                                    context = context,
                                    url = convertResult.value.data.imageUrl,
                                )
                                snackBarHost.showSnackBarWithDismiss(
                                    message = snackSavedMessage,
                                    actionLabel = snackBarCamera
                                )
                            }
                        }
                    )
                }
            }
        }
        if (playLottie) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LottieAnimation(
                        composition,
                        progress = { progress.value },
                        modifier = Modifier,
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AI 이미지 생성중...",
                        color = MaterialTheme.bbibbiScheme.white,
                        style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    )
                }
            }
        }
    }

}

suspend fun saveImageFromUrl(context: Context, url: String): Uri? = withContext(Dispatchers.IO) {
    try {
        // 1. 외부 URL에서 바이트 다운로드
        val bytes = URL(url).openStream().use { it.readBytes() }

        // 2. Bitmap 변환 (확장자 보고 포맷 정해도 됨)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // 3. MediaStore에 저장
        val filename = "${UUID.randomUUID()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/bbibbi") // 안드10+ 전용
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )

        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@Preview(
    showBackground = true,
    name = "MissionUploadPagePreview",
    showSystemUi = true
)
@Composable
fun MissionUploadPagePreview() {
    var isActive by remember { mutableStateOf(false) }
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                PostUploadPageTopBar()
                Spacer(modifier = Modifier.height(48.dp))
                PostUploadPageImagePreview(
                    previewImgUrl = null,
                    supportText = false,
                    onTapImageTextButton = {
                        isActive = true
                    }
                )
                Spacer(modifier = Modifier.height(48.dp))
                PostUploadPageUploadBar(
                    isIdle = true
                )
            }
        }

    }
}