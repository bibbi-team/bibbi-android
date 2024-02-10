package com.no5ing.bbibbi.presentation.feature.view.main.post_upload

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarCamera
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view_model.post.CreatePostViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.codePointLength
import com.no5ing.bbibbi.util.getErrorMessage
import kotlinx.coroutines.launch


@Composable
fun PostUploadPage(
    onDispose: () -> Unit,
    isUnsaveMode: Boolean = false,
    imageUrl: State<Uri?>,
    imageText: MutableState<String> = remember {
        mutableStateOf("")
    },
    textOverlayShown: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    createPostViewModel: CreatePostViewModel = hiltViewModel(),
) {
    val onDisposeWithSave = {
        createPostViewModel.clearTemporaryUri()
        onDispose()
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackBarHost = LocalSnackbarHostState.current
    val maxWord = 8
    val snackWarningMessage = stringResource(id = R.string.snack_bar_word_limit, maxWord)
    val snackNoSpaceMessage = stringResource(id = R.string.snack_bar_no_space)
    val snackSavedMessage = stringResource(id = R.string.snack_bar_saved)
    LaunchedEffect(Unit) {
        if (imageUrl.value == null) {
            onDispose()
        } else {
            if (!isUnsaveMode)
                createPostViewModel.saveTemporaryUri(imageUrl.value!!)
        }
    }
    val mixPanel = LocalMixpanelProvider.current
    val uploadResult = createPostViewModel.uiState.collectAsState()
    val snackErrorMessage = getErrorMessage(errorCode = uploadResult.value.errorCode)
    LaunchedEffect(uploadResult.value) {
        if (uploadResult.value.isReady()) {
            onDisposeWithSave()
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
                        onDispose = onDisposeWithSave,
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    PostUploadPageImagePreview(
                        previewImgUrl = imageUrl.value,
                        imageTextState = imageText,
                        onTapImageTextButton = {
                            mixPanel.track("Click_PhotoText")
                            textOverlayShown.value = true
                        }
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    PostUploadPageUploadBar(
                        isIdle = uploadResult.value.isIdle(),
                        onClickUpload = {
                            mixPanel.track("Click_UploadPhoto")
                            createPostViewModel.invoke(
                                Arguments(
                                    arguments = mapOf(
                                        "imageUri" to imageUrl.value.toString(),
                                        "content" to imageText.value
                                    )
                                )
                            )
                        },
                        onClickSave = {
                            val bitmap =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                                    ImageDecoder.decodeBitmap(
                                        ImageDecoder.createSource(
                                            context.contentResolver,
                                            imageUrl.value!!
                                        )
                                    )
                                else
                                    MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        imageUrl.value
                                    )
                            MediaStore.Images.Media.insertImage(
                                context.contentResolver,
                                bitmap,
                                imageText.value,
                                "bbibbi"
                            )
                            coroutineScope.launch {
                                snackBarHost.showSnackBarWithDismiss(
                                    message = snackSavedMessage,
                                    actionLabel = snackBarCamera
                                )
                            }
                        }
                    )
                }
            }
            AnimatedVisibility(
                textOverlayShown.value,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                PostUploadPageTextOverlay(
                    imageText = imageText,
                    onDispose = {
                        textOverlayShown.value = false
                    },
                    onTextInputChanged = { nextValue ->
                        if (nextValue.codePointLength() <= 8) {
                            if (nextValue.contains(" ")) {
                                coroutineScope.launch {
                                    snackBarHost.showSnackBarWithDismiss(
                                        message = snackNoSpaceMessage,
                                        actionLabel = snackBarWarning
                                    )
                                }
                                return@PostUploadPageTextOverlay
                            }
                            imageText.value = nextValue
                        } else {
                            coroutineScope.launch {
                                snackBarHost.showSnackBarWithDismiss(
                                    message = snackWarningMessage,
                                    actionLabel = snackBarWarning
                                )
                            }
                        }
                    },
                    onClearTextInput = {
                        imageText.value = ""
                    }
                )
            }

        }
    }

}

@Preview(
    showBackground = true,
    name = "PostUploadPagePreview",
    showSystemUi = true
)
@Composable
fun PostUploadPagePreview() {
    var isActive by remember { mutableStateOf(false) }
    val imageText = remember {
        mutableStateOf("글자테스트")
    }
    BBiBBiPreviewSurface {
        Box {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                PostUploadPageTopBar()
                Spacer(modifier = Modifier.height(48.dp))
                PostUploadPageImagePreview(
                    previewImgUrl = null,
                    imageTextState = imageText,
                    onTapImageTextButton = {
                        isActive = true
                    }
                )
                Spacer(modifier = Modifier.height(48.dp))
                PostUploadPageUploadBar(
                    isIdle = true
                )
            }
            AnimatedVisibility(
                isActive,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                PostUploadPageTextOverlay(
                    imageText = imageText,
                    onDispose = {
                        isActive = false
                    }
                )
            }
        }

    }
}