package com.no5ing.bbibbi.presentation.feature.view.main.change_family_name


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.family.ChangeFamilyNameViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.FamilyInfoViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.members.FamilyMemberViewModel
import com.no5ing.bbibbi.util.LocalSessionState
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ChangeFamilyNamePage(
    state: ChangeNicknamePageState = rememberChangeNicknamePageState(),
    changeFamilyNameViewModel: ChangeFamilyNameViewModel = hiltViewModel(),
    myFamilyInfoViewModel: FamilyInfoViewModel = hiltViewModel(),
    lastChangerMemberViewModel: FamilyMemberViewModel = hiltViewModel(),
    onDispose: () -> Unit,
) {
    val sessionState = LocalSessionState.current
    val textBoxFocus = remember { FocusRequester() }
    val familyInfo by myFamilyInfoViewModel.uiState.collectAsState()
    val uiState = changeFamilyNameViewModel.uiState.collectAsState()
    val snackBarHost = LocalSnackbarHostState.current
    val clearDialogEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        enabledState = clearDialogEnabled,
        title = "가족 방 이름을 초기화 할까요?",
        description = "홈 화면의 가족방 이름이 사라지고\nBibbi 로고로 바뀌어요",
        confirmRequest = {
            clearDialogEnabled.value = false
            changeFamilyNameViewModel.invoke(
                Arguments(
                    arguments = mapOf(
                        "familyId" to sessionState.familyId,
                        "familyName" to null,
                    )
                )
            )
        }
    )

    LaunchedEffect(Unit) {
        val priorNickName = ""
        state.nicknameTextState.value = priorNickName

        if (myFamilyInfoViewModel.isInitialize()) {
            myFamilyInfoViewModel.invoke(Arguments())
        }
    }

    LaunchedEffect(familyInfo) {
        if(familyInfo.isReady()) {
            state.nicknameTextState.value = familyInfo.data.familyName ?: ""

            if (familyInfo.data.familyNameEditorId != null) {
                lastChangerMemberViewModel.invoke(
                    Arguments(resourceId = familyInfo.data.familyNameEditorId)
                )
            }

        }
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
                changeFamilyNameViewModel.resetState()
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
            ChangeFamilyNamePageTopBar(
                onDispose = onDispose,
                onTapClear = {
                    clearDialogEnabled.value = true
                }
            )
            ChangeFamilyNamePageContent(
                textBoxFocus = textBoxFocus,
                nicknameTextState = state.nicknameTextState,
                invalidInputDescState = state.invalidInputDescState,
                isInvalidInputState = state.isInvalidInputState,
                onSubmit = {
                    changeFamilyNameViewModel.invoke(
                        Arguments(
                            arguments = mapOf(
                                "familyId" to sessionState.familyId,
                                "familyName" to it,
                            )
                        )
                    )
                },
                isProcessing = !uiState.value.isIdle(),
                lastChangeMemberState = lastChangerMemberViewModel.uiState,
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
            ChangeFamilyNamePageTopBar()
            ChangeFamilyNamePageContent(
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
                lastChangeMemberState = MutableStateFlow(APIResponse.unknownError()),
            )
        }
    }
}