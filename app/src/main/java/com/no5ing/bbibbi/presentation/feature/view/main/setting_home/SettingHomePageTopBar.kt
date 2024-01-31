package com.no5ing.bbibbi.presentation.feature.view.main.setting_home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.DisposableTopBar

@Composable
fun SettingHomePageTopBar(
    onDispose: () -> Unit = {},
) {
    DisposableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.setting_and_privacy)
    )
}