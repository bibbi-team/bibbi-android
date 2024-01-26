package com.no5ing.bbibbi.presentation.feature.view.main.setting_home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.no5ing.bbibbi.BuildConfig
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.data.model.APIResponse
import com.no5ing.bbibbi.data.model.auth.AppVersion
import com.no5ing.bbibbi.presentation.theme.bbibbiScheme
import com.no5ing.bbibbi.presentation.theme.bbibbiTypo

@Composable
fun SettingHomePageContent(
    appVersionState: APIResponse<AppVersion>,
    onTapMarketOpen: () -> Unit = {},
    onTapNotificationSetting: () -> Unit = {},
    onPrivacy: () -> Unit = {},
    onTerm: () -> Unit = {},
    onGroupQuit: () -> Unit = {},
    onQuit: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
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
        SettingHomePageItem(
            name = stringResource(
                id = R.string.setting_version_info,
                BuildConfig.VERSION_NAME
            ),
            onClick = {
                if (appVersionState.isReady() && !appVersionState.data.latest) {
                    onTapMarketOpen()
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
        SettingHomePageItem(
            name = stringResource(id = R.string.setting_notification_setting),
            onClick = onTapNotificationSetting,
        )
        SettingHomePageItem(
            name = stringResource(id = R.string.setting_privacy),
            onClick = onPrivacy,
        )
        SettingHomePageItem(
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
        SettingHomePageItem(
            name = stringResource(id = R.string.setting_logout),
            onClick = onLogout,
            isCritical = true,
        )
        SettingHomePageItem(
            name = stringResource(id = R.string.setting_group_quit),
            onClick = onGroupQuit,
            isCritical = true,
        )
        SettingHomePageItem(
            name = stringResource(id = R.string.setting_leave),
            onClick = onQuit,
            isCritical = true,
        )
    }
}