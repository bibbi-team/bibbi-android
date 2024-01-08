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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.state.setting.change_nickname.ChangeNicknamePageState
import com.no5ing.bbibbi.presentation.state.setting.change_nickname.rememberChangeNicknamePageState
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarSuccess
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.ui.theme.warningRed
import com.no5ing.bbibbi.presentation.viewmodel.members.ChangeNicknameViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    LaunchedEffect(Unit) {
        val priorNickName = changeNicknameViewModel.me?.name ?: ""
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
    Box(
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
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
                BasicTextField(
                    value = state.nicknameTextState.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                CTAButton(
                    text = stringResource(id = R.string.change_nickname_complete),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                    isActive = state.ctaButtonEnabledState.value && uiState.value.isIdle(),
                    onClick = {
                        focusHost.clearFocus()
                        changeNicknameViewModel.invoke(
                            Arguments(
                                arguments = mapOf(
                                    "nickName" to state.nicknameTextState.value
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