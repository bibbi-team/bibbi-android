package com.no5ing.bbibbi.presentation.view.post.upload

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.common.component.TextBubbleBox
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarCamera
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.post.CreatePostViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.codePointLength
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.toCodePointList
import kotlinx.coroutines.launch

const val defaultText = "여덟자로입력해요"

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
    val textBoxFocus = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
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
                    DisposableTopBar(
                        onDispose = { onDisposeWithSave() },
                        title = stringResource(id = R.string.upload_post),
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    modifier = Modifier
                                        .aspectRatio(1.0f)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(48.dp))
                                        .background(MaterialTheme.bbibbiScheme.backgroundHover),
                                    painter = rememberAsyncImagePainter(model = imageUrl.value),
                                    contentDescription = null,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .clickable {
                                    mixPanel.track("Click_PhotoText")
                                    textOverlayShown.value = true
                                }
                        ) {
                            if (imageText.value.isEmpty()) {
                                Image(
                                    modifier = Modifier
                                        .size(height = 41.dp, width = 36.dp),
                                    painter = painterResource(id = R.drawable.textbox_icon),
                                    contentDescription = null,
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    imageText.value.toCodePointList().forEach { character ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.Black.copy(alpha = 0.3f))
                                                .size(width = 28.dp, height = 41.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = character,
                                                color = MaterialTheme.bbibbiScheme.white,
                                                style = MaterialTheme.bbibbiTypo.headTwoBold,
                                            )
                                        }
                                    }

                                }
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        Box(modifier = Modifier.size(48.dp))
                        CTAButton(
                            text = stringResource(id = R.string.upload_image),
                            contentPadding = PaddingValues(horizontal = 60.dp, vertical = 15.dp),
                            onClick = {
                                mixPanel.track("Click_UploadPhoto")
                                createPostViewModel.invoke(
                                    Arguments(
                                        arguments = mapOf(
                                            "imageUri" to imageUrl.value.toString(),
                                            "content" to imageText.value
                                        )
                                    )
                                )
                            }
                        )
                        Image(
                            painter = painterResource(R.drawable.save_button),
                            contentDescription = null, // 필수 param
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
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
            }
            AnimatedVisibility(
                textOverlayShown.value,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                val focusState = remember {
                    mutableStateOf(false)
                }
                LaunchedEffect(Unit) {
                    textBoxFocus.requestFocus()
                    focusState.value = true
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .imePadding()
                    .clickable {
                        focusManager.clearFocus()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box {}
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.bbibbiScheme.backgroundPrimary)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BasicTextField(
                                    value = imageText.value,
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    modifier = Modifier
                                        .focusRequester(textBoxFocus)
                                        .onFocusChanged {
                                            if (!it.hasFocus && focusState.value) {
                                                textOverlayShown.value = false
                                            }
                                        },
                                    onValueChange = { nextValue ->
                                        if (nextValue.codePointLength() <= 8) {
                                            if (nextValue.contains(" ")) {
                                                coroutineScope.launch {
                                                    snackBarHost.showSnackBarWithDismiss(
                                                        message = snackNoSpaceMessage,
                                                        actionLabel = snackBarWarning
                                                    )
                                                }
                                                return@BasicTextField
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
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done,
                                    ),
                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.bbibbiScheme.white
                                    ),
                                    cursorBrush = Brush.verticalGradient(
                                        0.00f to MaterialTheme.bbibbiScheme.button,
                                        1.00f to MaterialTheme.bbibbiScheme.button,
                                    ),
                                    maxLines = 1,
                                    decorationBox = {
                                        if (imageText.value.isEmpty()) {
                                            Text(
                                                text = defaultText,
                                                color = MaterialTheme.bbibbiScheme.textSecondary,
                                                fontSize = 16.sp,
                                            )
                                        } else {
                                            it()
                                        }
                                    },
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.clear_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            imageText.value = ""
                                        },
                                    tint = MaterialTheme.bbibbiScheme.icon
                                )
                            }

                        }

                    }
                    TextBubbleBox(
                        text = imageText.value.ifEmpty { defaultText },
                        textStyle = if (imageText.value.isEmpty()) MaterialTheme.bbibbiTypo.headOne.copy(
                            fontWeight = FontWeight.Normal
                        ) else MaterialTheme.bbibbiTypo.headOne,
                        textColor = if (imageText.value.isEmpty()) MaterialTheme.bbibbiScheme.textSecondary else MaterialTheme.bbibbiScheme.white,
                    )
                }
            }

        }
    }

}