package com.no5ing.bbibbi.presentation.feature.view.register.nickname


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.feature.state.register.nickname.RegisterNickNamePageState
import com.no5ing.bbibbi.presentation.feature.state.register.nickname.rememberRegisterNickNamePageState
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegisterNickNamePage(
    state: RegisterNickNamePageState = rememberRegisterNickNamePageState(),
    onNextPage: () -> Unit,
) {
    val textBoxFocus = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val maxWord = 9
    val wordExceedMessage = stringResource(id = R.string.register_nickname_word_below_n, maxWord)
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
            Spacer(modifier = Modifier)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_nickname_enter_nickname),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = state.nicknameTextState.value,
                    interactionSource = interactionSource,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                    onValueChange = {
                        val prevWord = state.nicknameTextState.value
                        state.ctaButtonEnabledState.value = it.length >= 2
                        if (it.length > maxWord) {
                            state.isInvalidInputState.value = true
                            state.invalidInputDescState.value = wordExceedMessage
                        } else if (prevWord != it) {
                            state.nicknameTextState.value = it
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
                                            style = MaterialTheme.bbibbiTypo.title,
                                        )
                                    }

                                }
                            }
                        }


                    },
                    cursorBrush = Brush.verticalGradient(
                        0.00f to MaterialTheme.bbibbiScheme.button,
                        1.00f to MaterialTheme.bbibbiScheme.button,
                    ),
                    textStyle = MaterialTheme.bbibbiTypo.title.copy(
                        textAlign = TextAlign.Center,
                        color = if (state.isInvalidInputState.value)
                            MaterialTheme.bbibbiScheme.warningRed
                        else
                            MaterialTheme.bbibbiScheme.textPrimary
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
                            tint = MaterialTheme.bbibbiScheme.warningRed,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.invalidInputDescState.value,
                            color = MaterialTheme.bbibbiScheme.warningRed,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular
                        )
                    }

                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.register_nickname_description),
                    color = MaterialTheme.bbibbiScheme.icon,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
                CTAButton(
                    text = stringResource(id = R.string.register_continue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    isActive = state.nicknameTextState.value.length in 2..maxWord,
                    onClick = onNextPage,
                )
            }

        }
    }
    LaunchedEffect(Unit) {
        textBoxFocus.requestFocus()
    }
}