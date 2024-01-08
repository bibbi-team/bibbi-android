package com.no5ing.bbibbi.presentation.ui.feature.setting.home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.OperationStatus
import com.no5ing.bbibbi.data.repository.Arguments
import com.no5ing.bbibbi.presentation.ui.common.component.DisposableTopBar
import com.no5ing.bbibbi.presentation.ui.feature.dialog.CustomAlertDialog
import com.no5ing.bbibbi.presentation.ui.showSnackBarWithDismiss
import com.no5ing.bbibbi.presentation.ui.snackBarInfo
import com.no5ing.bbibbi.presentation.ui.theme.criticalRed
import com.no5ing.bbibbi.presentation.viewmodel.auth.LogoutViewModel
import com.no5ing.bbibbi.presentation.viewmodel.auth.QuitViewModel
import com.no5ing.bbibbi.util.LocalSnackbarHostState
import com.no5ing.bbibbi.util.emptyPermissionState
import com.no5ing.bbibbi.util.localResources
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingHomePage(
    onDispose: () -> Unit,
    onLogout: () -> Unit,
    onQuitCompleted: () -> Unit,
    onTerm: () -> Unit,
    logoutViewModel: LogoutViewModel = hiltViewModel(),
    quitViewModel: QuitViewModel = hiltViewModel(),
) {
    val logOutState = logoutViewModel.uiState.collectAsState()
    val quitState = quitViewModel.uiState.collectAsState()
    val resources = localResources()
    val snackBarHost = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(logOutState.value) {
        when (logOutState.value) {
            OperationStatus.SUCCESS -> onLogout()
            else -> {}
        }
    }
    LaunchedEffect(quitState.value) {
        when (quitState.value) {
            OperationStatus.SUCCESS -> onQuitCompleted()
            else -> {}
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

    val quitModalEnabled = remember { mutableStateOf(false) }
    CustomAlertDialog(
        title = stringResource(id = R.string.dialog_quit_title),
        description = stringResource(id = R.string.dialog_quit_message),
        enabledState = quitModalEnabled,
        confirmMessage = stringResource(id = R.string.dialog_quit_confirm),
        confirmRequest = {
            quitViewModel.invoke(Arguments())
        }
    )

    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
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
                        .padding(bottom = 8.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_version_info),
                    onClick = {},
                    rightButton = {
                        Text(
                            text = BuildConfig.VERSION_NAME,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                    name = stringResource(id = R.string.setting_terms_of_service),
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
                        .padding(bottom = 8.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_logout),
                    onClick = {
                        logoutModalEnabled.value = true
                    },
                    isCritical = true,
                )
                SettingItem(
                    name = stringResource(id = R.string.setting_leave),
                    onClick = {
                        quitModalEnabled.value = true
                    },
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
            tint = MaterialTheme.colorScheme.onSurface,
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
            color = if (isCritical) criticalRed else MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp,
        )
        rightButton()
    }
}