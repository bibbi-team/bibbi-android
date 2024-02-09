package com.no5ing.bbibbi.presentation.feature.view.main.setting_home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.OperationStatus
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.component.BBiBBiPreviewSurface
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarInfo
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.feature.view_model.auth.LogoutViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveAppVersionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.QuitFamilyViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.emptyPermissionState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.openMarket
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingHomePage(
    onDispose: () -> Unit,
    onLogOutCompleted: () -> Unit,
    onQuit: () -> Unit,
    onFamilyQuitCompleted: () -> Unit,
    onTerm: () -> Unit,
    onPrivacy: () -> Unit,
    logoutViewModel: LogoutViewModel = hiltViewModel(),
    familyQuitViewModel: QuitFamilyViewModel = hiltViewModel(),
    retrieveAppVersionViewModel: RetrieveAppVersionViewModel = hiltViewModel(),
) {
    val logOutState = logoutViewModel.uiState.collectAsState()
    val mixPanel = LocalMixpanelProvider.current
    val familyQuitState by familyQuitViewModel.uiState.collectAsState()
    val appVersionState by retrieveAppVersionViewModel.uiState.collectAsState()
    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current
    LaunchedEffect(logOutState.value) {
        when (logOutState.value) {
            OperationStatus.SUCCESS -> onLogOutCompleted()
            else -> {}
        }
    }

    LaunchedEffect(appVersionState) {
        if (appVersionState.isIdle()) {
            retrieveAppVersionViewModel.invoke(Arguments())
        }
    }


    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    else
        remember { emptyPermissionState }

    val logoutModalEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_logout_title),
        description = stringResource(id = R.string.dialog_logout_message),
        enabledState = logoutModalEnabled,
        confirmMessage = stringResource(id = R.string.dialog_logout_confirm),
        confirmRequest = {
            logoutViewModel.invoke(Arguments())
        }
    )


    val familyQuitModalEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_quit_group_title),
        description = stringResource(id = R.string.dialog_quit_group_message),
        enabledState = familyQuitModalEnabled,
        confirmMessage = stringResource(id = R.string.dialog_quit_group_confirm),
        confirmRequest = {
            mixPanel.track("Click_LeaveGroup")
            familyQuitViewModel.invoke(Arguments())
        }
    )

    LaunchedEffect(familyQuitState) {
        when (familyQuitState.status) {
            APIResponse.Status.SUCCESS -> onFamilyQuitCompleted()
            APIResponse.Status.ERROR -> {
                familyQuitViewModel.resetState()
                familyQuitModalEnabled.value = false
                val errMessage = resources.getErrorMessage(familyQuitState.errorCode)
                snackBarHost.showSnackBarWithDismiss(
                    message = errMessage,
                    actionLabel = snackBarWarning,
                )
            }

            else -> {
                Timber.d("Failed")
            }
        }
    }

    val context = LocalContext.current
    BBiBBiSurface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingHomePageTopBar(
                onDispose = onDispose,
            )
            Spacer(modifier = Modifier.height(32.dp))
            SettingHomePageContent(
                appVersionState = appVersionState,
                onVersionLongTap = {
                    snackBarHost.showSnackBarWithDismiss(
                        resources.getString(R.string.build_number_info, BuildConfig.VERSION_CODE),
                        snackBarInfo
                    )
                },
                onTapMarketOpen = {
                    context.openMarket()
                },
                onTapNotificationSetting = {
                    if (notificationPermission.status.isGranted) {
                        snackBarHost.showSnackBarWithDismiss(
                            resources.getString(R.string.snack_bar_alreday_accepted),
                            snackBarInfo
                        )
                    } else {
                        val settingsIntent =
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(
                                    Settings.EXTRA_APP_PACKAGE,
                                    BuildConfig.APPLICATION_ID
                                )
                        context.startActivity(settingsIntent)
                    }
                },
                onPrivacy = onPrivacy,
                onTerm = onTerm,
                onGroupQuit = {
                    familyQuitModalEnabled.value = true
                },
                onQuit = onQuit,
                onLogout = {
                    logoutModalEnabled.value = true
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "SettingHomePagePreview",
    showSystemUi = true
)
@Composable
fun SettingHomePagePreview() {
    BBiBBiPreviewSurface {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SettingHomePageTopBar()
            Spacer(modifier = Modifier.height(32.dp))
            SettingHomePageContent(
                appVersionState = APIResponse.loading(),
            )
        }
    }
}

