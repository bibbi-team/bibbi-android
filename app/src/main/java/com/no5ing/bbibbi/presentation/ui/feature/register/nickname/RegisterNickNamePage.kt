package com.no5ing.bbibbi.presentation.ui.feature.register.nickname


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.state.register.nickname.RegisterNickNamePageState
import com.no5ing.bbibbi.presentation.state.register.nickname.rememberRegisterNickNamePageState
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.theme.warningRed

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegisterNickNamePage(
    state: RegisterNickNamePageState = rememberRegisterNickNamePageState(),
    onNextPage: () -> Unit,
) {
    val textBoxFocus = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val maxWord = 10
    val wordExceedMessage = stringResource(id = R.string.register_nickname_word_below_n, maxWord)
    Box(
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
            Spacer(modifier = Modifier)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_nickname_enter_nickname),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
                BasicTextField(
                    value = state.nicknameTextState.value,
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                    onValueChange = {
                        state.nicknameTextState.value = it
                        state.ctaButtonEnabledState.value = it.length >= 2
                        if (it.length > maxWord) {
                            state.isInvalidInputState.value = true
                            state.invalidInputDescState.value = wordExceedMessage
                            state.ctaButtonEnabledState.value = false
                        } else {
                            state.isInvalidInputState.value = false
                        }
                    },
                    singleLine = true,
                    decorationBox = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box {
                                it()
                                if (state.nicknameTextState.value.isEmpty()) {
                                    Box(modifier = Modifier.align(Alignment.Center)) {
                                        Text(
                                            text = stringResource(id = R.string.register_nickname_sample_text),
                                            textAlign = TextAlign.Center,
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }

                                }
                            }
                        }


                    },
                    cursorBrush = Brush.verticalGradient(
                        0.00f to MaterialTheme.colorScheme.surface,
                        1.00f to MaterialTheme.colorScheme.surface,
                    ),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.isInvalidInputState.value)
                            warningRed
                        else
                            MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .focusRequester(textBoxFocus),
                )
                if (state.isInvalidInputState.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.warning_circle_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp),
                            tint = warningRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.invalidInputDescState.value,
                            fontSize = 16.sp,
                            color = warningRed,
                        )
                    }

                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_nickname_description),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
                CTAButton(
                    text = stringResource(id = R.string.register_continue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    isActive = state.ctaButtonEnabledState.value,
                    onClick = onNextPage,
                )
            }

        }
    }
    LaunchedEffect(Unit) {
        textBoxFocus.requestFocus()
    }
}