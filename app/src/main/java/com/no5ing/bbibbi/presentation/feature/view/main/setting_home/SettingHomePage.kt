package com.no5ing.bbibbi.presentation.feature.view.main.setting_home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.no5ing.bbibbi.presentation.component.BBiBBiSurface
import com.no5ing.bbibbi.presentation.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.feature.view.common.CustomAlertDialog
import com.no5ing.bbibbi.presentation.component.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.component.snackBarInfo
import com.no5ing.bbibbi.presentation.component.snackBarWarning
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo
import com.no5ing.bbibbi.presentation.feature.view_model.auth.LogoutViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.auth.RetrieveAppVersionViewModel
import com.no5ing.bbibbi.presentation.feature.view_model.family.QuitFamilyViewModel
import com.no5ing.bbibbi.util.LocalMixpanelProvider
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.emptyPermissionState
import com.no5ing.bbibbi.util.getErrorMessage
import com.no5ing.bbibbi.util.localResources
import com.no5ing.bbibbi.util.openMarket
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingHomePage(
    onDispose: () -> Unit,
    onLogout: () -> Unit,
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
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(logOutState.value) {
        when (logOutState.value) {
            OperationStatus.SUCCESS -> onLogout()
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
            DisposableTopBar(
                onDispose = onDispose,
                title = stringResource(id = R.string.setting_and_privacy)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.setting_account_and_permission),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.bbibbiScheme.icon,
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                )
                val versionInfoText = if (appVersionState.isReady()) {
                    if (appVersionState.data.latest) {
                        stringResource(id = R.string.setting_version_info_latest)
                    } else {
                        stringResource(id = R.string.setting_version_info_require_update)
                    }
                } else stringResource(id = R.string.setting_version_info_querying)
                SettingItem(
                    name = stringResource(
                        id = R.string.setting_version_info,
                        BuildConfig.VERSION_NAME
                    ),
                    onClick = {
                        if (appVersionState.isReady() && !appVersionState.data.latest) {
                            context.openMarket()
                        }
                    },
                    rightButton = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = versionInfoText,
                                color = MaterialTheme.bbibbiScheme.icon,
                                style = MaterialTheme.bbibbiTypo.bodyTwoRegular,
                            )
                            if (appVersionState.isReady() && !appVersionState.data.latest) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_right),
                                    contentDescription = null,
                                    tint = MaterialTheme.bbibbiScheme.icon,
                                )
                            }
                        }
                    }
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_notification_setting),
                    onClick = {
                        if (notificationPermission.status.isGranted) {
                            coroutineScope.launch {
                                snackBarHost.showSnackBarWithDismiss(
                                    resources.getString(R.string.snack_bar_alreday_accepted),
                                    snackBarInfo
                                )
                            }
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

                    }
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_privacy),
                    onClick = onPrivacy,
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_terms),
                    onClick = onTerm,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.setting_login),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp),
                    style = MaterialTheme.bbibbiTypo.bodyOneRegular,
                    color = MaterialTheme.bbibbiScheme.icon
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_logout),
                    onClick = {
                        logoutModalEnabled.value = true
                    },
                    isCritical = true,
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_group_quit),
                    onClick = {
                        familyQuitModalEnabled.value = true
                    },
                    isCritical = true,
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_leave),
                    onClick = onQuit,
                    isCritical = true,
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    onClick: () -> Unit,
    isCritical: Boolean = false,
    rightButton: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = null,
            tint = MaterialTheme.bbibbiScheme.icon,
        )
    }
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = name,
            color = if (isCritical) MaterialTheme.bbibbiScheme.warningRed else MaterialTheme.bbibbiScheme.textPrimary,
            style = MaterialTheme.bbibbiTypo.bodyOneRegular,
        )
        rightButton()
    }
}