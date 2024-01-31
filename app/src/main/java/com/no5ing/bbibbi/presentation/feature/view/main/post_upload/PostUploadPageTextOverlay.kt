package com.no5ing.bbibbi.presentation.feature.view.main.post_upload

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.TextBubbleBox
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun PostUploadPageTextOverlay(
    imageText: State<String>,
    onDispose: () -> Unit = {},
    onTextInputChanged: (String) -> Unit = {},
    onClearTextInput: () -> Unit = {},
) {
    val defaultText = stringResource(id = R.string.post_upload_sample_text)
    val textBoxFocus = remember { FocusRequester() }
    val focusState = remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
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
                                    onDispose()
                                }
                            },
                        onValueChange = { nextValue ->
                            onTextInputChanged(nextValue)
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
                                onClearTextInput()
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