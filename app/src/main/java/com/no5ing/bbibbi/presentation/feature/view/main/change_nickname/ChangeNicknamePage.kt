package com.no5ing.bbibbi.presentation.feature.view.main.change_nickname


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarSuccess
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.state.setting.change_nickname.ChangeNicknamePageState
import com.no5ing.bbibbi.presentation.feature.state.setting.change_nickname.rememberChangeNicknamePageState
import com.no5ing.bbibbi.presentation.feature.view_model.members.ChangeNicknameViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage

@Composable
fun ChangeNicknamePage(
    state: ChangeNicknamePageState = rememberChangeNicknamePageState(),
    changeNicknameViewModel: ChangeNicknameViewModel = hiltViewModel(),
    onDispose: () -> Unit,
) {
    val sessionState = LocalSessionState.current
    val textBoxFocus = remember { FocusRequester() }
    val uiState = changeNicknameViewModel.uiState.collectAsState()
    val snackBarHost = LocalSnackbarHostState.current

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
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            ChangeNicknamePageTopBar(
                onDispose = onDispose,
            )
            ChangeNicknamePageContent(
                textBoxFocus = textBoxFocus,
                nicknameTextState = state.nicknameTextState,
                invalidInputDescState = state.invalidInputDescState,
                isInvalidInputState = state.isInvalidInputState,
                onSubmit = {
                    changeNicknameViewModel.invoke(
                        Arguments(
                            arguments = mapOf(
                                "nickName" to it,
                                "memberId" to sessionState.memberId,
                            )
                        )
                    )
                },
                isProcessing = !uiState.value.isIdle()
            )
        }
    }
    LaunchedEffect(Unit) {
        textBoxFocus.requestFocus()
    }
}

@Preview(
    showBackground = true,
    name = "ChangeNicknamePagePreview",
    showSystemUi = true
)
@Composable
fun ChangeNicknamePagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ChangeNicknamePageTopBar()
            ChangeNicknamePageContent(
                textBoxFocus = FocusRequester(),
                nicknameTextState = remember {
                    mutableStateOf("안녕하세요")
                },
                invalidInputDescState = remember {
                    mutableStateOf("이름")
                },
                isInvalidInputState = remember {
                    mutableStateOf(false)
                },
                isProcessing = false,
            )
        }
    }
}