package com.no5ing.bbibbi.presentation.feature.view.main.quit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.button.CTAButton
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.auth.QuitViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources

val quitReasons = listOf(
    "NO_NEED_TO_SHARE_DAILY" to "가족과 일상을 공유하고 싶지 않아서",
    "FAMILY_MEMBER_NOT_USING" to "가족 구성원이 참여하지 않아서",
    "NO_PREFER_WIDGET_OR_NOTIFICATION" to "위젯이나 알림 기능을 선호하지 않아서",
    "SERVICE_UX_IS_BAD" to "서비스 이용이 어렵거나 불편해서",
    "NO_FREQUENTLY_USE" to "자주 사용하지 않아서",
)

@Composable
fun QuitPage(
    onDispose: () -> Unit,
    onQuitSuccess: () -> Unit,
    quitViewModel: QuitViewModel = hiltViewModel(),
) {
    val mixPanel = LocalMixpanelProvider.current
    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current

    val currentSelection = remember {
        mutableStateListOf<Int>()
    }

    val quitModalEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_quit_title),
        description = stringResource(id = R.string.dialog_quit_message),
        enabledState = quitModalEnabled,
        confirmMessage = stringResource(id = R.string.dialog_quit_confirm),
        confirmRequest = {
            mixPanel.track("Click_Withdrawal")
            quitViewModel.invoke(
                Arguments(
                    arguments = mapOf("reasons" to currentSelection.joinToString(
                        ","
                    ) { quitReasons[it].first })
                )
            )
        }
    )
    val quitState by quitViewModel.uiState.collectAsState()
    LaunchedEffect(quitState) {
        when (quitState.status) {
            APIResponse.Status.SUCCESS -> onQuitSuccess()
            APIResponse.Status.ERROR -> {
                quitViewModel.resetState()
                quitModalEnabled.value = false
                val errMessage = resources.getErrorMessage(quitState.errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errMessage,
                    actionLabel = snackBarWarning,
                )
            }

            else -> {}
        }
    }
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                QuitPageTopBar(
                    onDispose = onDispose,
                )
                QuitPageContent(
                    currentSelection = currentSelection
                )
            }
            CTAButton(
                text = stringResource(id = R.string.quit_button),
                onClick = {
                    quitModalEnabled.value = true
                },
                isActive = currentSelection.isNotEmpty(),
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                    )
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(vertical = 18.dp),
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "QuitPagePreview",
    showSystemUi = true
)
@Composable
fun QuitPagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                QuitPageTopBar()
                QuitPageContent(
                    currentSelection = remember {
                        mutableStateListOf<Int>()
                    }
                )
            }
            CTAButton(
                text = stringResource(id = R.string.quit_button),
                isActive = true,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(vertical = 18.dp),
            )

        }
    }
}