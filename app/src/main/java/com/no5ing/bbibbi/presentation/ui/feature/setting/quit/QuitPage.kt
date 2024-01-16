package com.no5ing.bbibbi.presentation.ui.feature.setting.quit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.button.CTAButton
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.feature.dialog.CustomAlertDialog
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarWarning
import com.no5ing.bbibbi.presentation.ui.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.viewmodel.auth.QuitViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources

@Composable
fun QuitPage(
    onDispose: () -> Unit,
    onQuitSuccess: () -> Unit,
    quitViewModel: QuitViewModel = hiltViewModel(),
) {
    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current
    val quitReasons = remember {
        listOf(
            "NO_NEED_TO_SHARE_DAILY" to "가족과 일상을 공유하고 싶지 않아서",
            "FAMILY_MEMBER_NOT_USING" to "가족 구성원이 참여하지 않아서",
            "NO_PREFER_WIDGET_OR_NOTIFICATION" to "위젯이나 알림 기능을 선호하지 않아서",
            "SERVICE_UX_IS_BAD" to "서비스 이용이 어렵거나 불편해서",
            "NO_FREQUENTLY_USE" to "자주 사용하지 않아서",
        )
    }
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
            quitViewModel.invoke(Arguments(arguments = mapOf("reasons" to currentSelection.joinToString(
                ","
            ) { quitReasons[it].first })))
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
    Box(
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
                DisposableTopBar(onDispose = onDispose, title = stringResource(id = R.string.quit_title))
                Column(
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.quit_heading_one),
                        color = MaterialTheme.bbibbiScheme.textSecondary
                    )
                    Text(
                        text = stringResource(id = R.string.quit_heading_two),
                        color = MaterialTheme.bbibbiScheme.textPrimary,
                    )
                    Text(
                        text = stringResource(id = R.string.quit_minimum_one),
                        color = MaterialTheme.bbibbiScheme.textSecondary
                    )
                    Column {
                        quitReasons.forEachIndexed { index, pair ->
                            Row {
                                Checkbox(
                                    checked = currentSelection.contains(index),
                                    onCheckedChange = {
                                        if (!it) {
                                            currentSelection.remove(index)
                                        } else {
                                            currentSelection.add(index)
                                        }
                                    })
                                Text(
                                    text = pair.second,
                                    color = MaterialTheme.bbibbiScheme.textPrimary,
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                )
            ) {
                CTAButton(
                    text = stringResource(id = R.string.quit_button),
                    onClick = {
                        quitModalEnabled.value = true
                    },
                    isActive = currentSelection.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(vertical = 18.dp),
                )
            }
        }
    }
}