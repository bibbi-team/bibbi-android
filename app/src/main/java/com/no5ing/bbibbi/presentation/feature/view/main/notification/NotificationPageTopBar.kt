package com.no5ing.bbibbi.presentation.feature.view.main.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.DisposableTopBar

@Composable
fun NotificationPageTopBar(
    onDispose: () -> Unit = {},
) {
    DisposableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.notifcation_page_title)
    )
}