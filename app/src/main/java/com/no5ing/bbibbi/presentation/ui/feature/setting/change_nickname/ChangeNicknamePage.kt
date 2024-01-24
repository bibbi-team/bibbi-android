package com.no5ing.bbibbi.presentation.ui.feature.setting.change_nickname


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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.setting.change_nickname.ChangeNicknamePageState
import com.no5ing.bbibbi.presentation.state.setting.change_nickname.rememberChangeNicknamePageState
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarSuccess
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.viewmodel.members.ChangeNicknameViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import timber.log.Timber

@Composable
fun ChangeNicknamePage(
    state: ChangeNicknamePageState = rememberChangeNicknamePageState(),
    changeNicknameViewModel: ChangeNicknameViewModel = hiltViewModel(),
    onDispose: () -> Unit,
) {
    val textBoxFocus = remember { FocusRequester() }
    val maxWord = 10
    val wordExceedMessage = stringResource(id = R.string.register_nickname_word_below_n, maxWord)
    val uiState = changeNicknameViewModel.uiState.collectAsState()
    val snackBarHost = LocalSnackbarHostState.current
    val focusHost = LocalFocusManager.current
    val sessionState = LocalSessionState.current
    LaunchedEffect(Unit) {
        val priorNickName = ""
        state.nicknameTextState.value = priorNickName
    }
    val successMessage = stringResource(id = R.string.change_nickname_completed)
    val errorMessage = getErrorMessage(errorCode = uiState.value.errorCode)
    LaunchedEffect(uiState.value) {
        when (uiState.value.status) {
            is APIResponse.Status.SUCCESS -> {
                snackBarHost.showSnackBarWithDismiss(
                    successMessage,
                    actionLabel = snackBarSuccess,
                )
                onDispose()
            }

            is APIResponse.Status.ERROR -> {
                changeNicknameViewModel.resetState()
                snackBarHost.showSnackBarWithDismiss(
                    errorMessage,
                    actionLabel = snackBarWarning,
                )
            }

            else -> {}
        }
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.change_nickname),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.change_nickname_description),
                    color = MaterialTheme.bbibbiScheme.textSecondary,
                    style = MaterialTheme.bbibbiTypo.headTwoBold,
                )
                BasicTextField(
                    value = state.nicknameTextState.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        val prevWord = state.nicknameTextState.value
                        state.ctaButtonEnabledState.value = it.length >= 2
                        if (it.length > maxWord) {
                            state.isInvalidInputState.value = true
                            state.invalidInputDescState.value = wordExceedMessage
                        } else if(prevWord != it) {
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
                    modifier = Modifier.focusRequester(textBoxFocus),
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
                            tint = MaterialTheme.bbibbiScheme.warningRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.invalidInputDescState.value,
                            color = MaterialTheme.bbibbiScheme.warningRed,
                            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                        )
                    }

                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CTAButton(
                    text = stringResource(id = R.string.change_nickname_complete),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    isActive = state.nicknameTextState.value.length in 2..maxWord && uiState.value.isIdle(),
                    onClick = {
                        focusHost.clearFocus()
                        changeNicknameViewModel.invoke(
                            Arguments(
                                arguments = mapOf(
                                    "nickName" to state.nicknameTextState.value,
                                    "memberId" to sessionState.memberId,
                                )
                            )
                        )
                    },
                )
            }

        }
    }
    LaunchedEffect(Unit) {
        textBoxFocus.requestFocus()
    }
}