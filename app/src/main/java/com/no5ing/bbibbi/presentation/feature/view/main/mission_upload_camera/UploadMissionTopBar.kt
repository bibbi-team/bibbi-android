package com.no5ing.bbibbi.presentation.feature.view.main.mission_upload_camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.no5ing.bbibbi.R
import com.no5ing.bbibbi.presentation.component.ClosableTopBar

@Composable
fun UploadMissionTopBar(
    onDispose: () -> Unit = {},
) {
    ClosableTopBar(
        onDispose = onDispose,
        title = stringResource(id = R.string.mission_camera_title),
    )
}